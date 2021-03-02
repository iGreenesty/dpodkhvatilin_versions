/*! UTF-8 */

/**
 * Дата: 30.10.2014
 * Изменено: 15.09.2015
 * SCRIPTSD4000893
 * Автор: dshirykalov
 * Назначение: Обработчик почты для конфигурации ITSM365
 */

import java.util.regex.Pattern

//ПАРАМЕТРЫ---------------------------------------------------------------------------------------------------

// Набор строк-префиксов, после которых ожидается номер заявки
// Если список пуст, то префикс для поиска номера заявки не используется.
PREFIXES = ['№', 'No', 'No.', '�']

// Код уровня влияния (низкий)
IMPACT_CODE = 'I0'

// Код уровня срочности (низкий)
URGENCY_CODE = 'U2'

// Соответствие e-mail адресов отправителей кодам типов заявок
SENDER_2_TYPE_CODE = [:]

// Код параметра "Регистрировать заявки от незарегистрированных пользователей"
REG_SC_UNREG_USER_ATTR_CODE = 'isSCFrNotReg'

// Код типа заявки по умолчанию
DEFAULT_TYPE_CODE = 'call'

// Уникальный номер соглашения по умолчанию
AGREEMENT_INVENTORY_NUMBER = 'B001'

// Уникальный номер услуги по умолчанию
SERVICE_INVENTORY_NUMBER = '00'

// Коды типов сотрудников, на которых можно регистрировать заявку. Если пусто, то на любых.
EMPLOYEE_TYPES = []  // типы записываются в кавычках через запятую.

// Фамилия служебного сотрудника
DEFAULT_EMPLOYEE_NAME = 'Служебный'

// Требуется ли добавлять в список файлов заявки файл с письмом в формате .eml
ADD_ORIGINAL_MAIL = false

// Код элемента "По почте" справочника "Способ обращения"
REQUEST_WAY_ELEMENT_CODE = 'email'

// Требуется ли оповещать отправителя об отрицательном результате обработки письма
// (оповещения о положительном результате через данный скрипт не отправляются)
NOTIFY_ABOUT_ERRORS = true

// Код атрибута комментария для прикрепления файлов
COMMENT_FILE_ATTR = 'files'

// Код атрибута "Обрабатывать автоматические письма" в компании
ISAUTOMAIL_ATTR = 'isAutoMails'

// Разделитель, добавляемый в оповещение
OUTGOING_COMMENT_DELIMITER = '---↑ При ответе добавьте комментарий выше этой строки ↑---'

// Прочие поддерживаемые разделители
OTHER_COMMENT_DELIMITERS = [
	'<table id="background-table"',
	'При ответе добавьте комментарий выше этой строки',
	'&uarr;',
	'↑',
  	'При ответе добавьте комментарий выше'
]

// Логирование событий обработчика, true - включено, false - выключено
LOGGING_IS_ENABLED = true

// Уровень логирования (если логирование включено)
// Доступные уровни логирования: 'error', 'warn', 'info', 'debug', 'trace'
LOG_LEVEL = 'info'

//ОСНОВНОЙ БЛОК----------------------------------------------------------------------------------------------

mailLogMsg = '<ol style="line-height: 1rem">';
log('Начало обработки.')

// Текст оповещения для отправителя письма (заполняется далее)
def notificationText

// Ошибка, которая может возникнуть при создании/изменении
def maybeError

try
{
	// Если письмо отклонено при первоначальной обработке (чёрные списки и т.д.)
	
	if (result.rejected)
	{
		throw new ScriptError(ErrorType.REJECT, result.rejectMessage)
	}
  
  	// Проверка на авто письмо, если она включена
  	def isAutoMailOff = utils.get('root', [:])[(ISAUTOMAIL_ATTR)]
    if(!isAutoMailOff) {
      log('Проверка на автоматическое письмо...')
      if(isAutoAnswerByMessage(message)) {
        throw new ScriptError(ErrorType.AUTO_MAIL, '')
      }
      log('Письмо не автоматическое')
    } else {
      log('Проверка на автоматические письма отключена')
    }
	
	// Метод модифицирует html-содержимое письма для корректного отображения вложенных картинок
	api.mail.helper.replaceReferencesToAttachments(message)
  	if(message && api.string.isEmptyTrim(message.getBody()) == false) {
      try {
        api.preview.shrinkImages(message, false)
      } catch (Exception e) {
        logger.error("[Обработка почты] Ошибка с shrinkImage", e)
        log("Возникла ошибка метода shrinkImage")
      }
    }
	
	def serviceCall = null
	
	// Поиск номера заявки в теме письма
	def scNumber = getNumberAfterPrefix(PREFIXES, message.subject)
	
	// Если номер заявки в теме письма найден и это номер без ведущих нулей
	if (scNumber != null && scNumber.toInteger().toString() == scNumber)
	{
		log("В теме письма найден номер заявки: \"${scNumber}\".")
		
		// Поиск заявки по номеру
		serviceCall = findOne('serviceCall', ['number': scNumber])
		
		log(serviceCall ? "Заявка по номеру \"${scNumber}\" найдена." : "Заявка по номеру \"${scNumber}\" не найдена.")
      	
      	if(serviceCall && serviceCall.state == 'closed') {
          log("Заявка №${scNumber} закрыта. Регистрируем новую...")
          serviceCall = null
        }
	}
	else
	{
		log('Тема письма не содержит номера заявки.')
	}
  
  	// Разрешена ли регистрация заявок с неизвестных адресов
  	def allowedFromUnknownAddress = utils.get('root', [:])[REG_SC_UNREG_USER_ATTR_CODE]
	
	// Если заявка не найдена
	if (serviceCall == null)
	{
		// Добавить новую заявку
		log('Получение параметров для создания новой заявки...')
		
		// Заполнить атрибуты создаваемой заявки
		def attrs = [:]

		// Определить контрагента заявки подходящего типа по адресу из письма
		attrs.client = getEmployee(message.from.address, EMPLOYEE_TYPES, DEFAULT_EMPLOYEE_NAME)
      	
		// Если контрагент не пуст и контрагентом назначен не служебный сотрудник
		if (attrs.client && attrs.client.lastName != DEFAULT_EMPLOYEE_NAME)
		{
			// Имя контактного лица заполнить ФИО контрагента
			attrs.clientName = attrs.client.title
			// Контактный email заполнить email-ом контрагента
			attrs.clientEmail = attrs.client.email
			// Контактный телефон заполнить списком телефонов контрагента
			attrs.clientPhone = attrs.client.phonesIndex
		}
		// Если контрагент не определён и разрешена регистрация с неизв. адресов
		else if(allowedFromUnknownAddress) 
		{
			// Определяем компанию по домену
          	def ouByDomain = getOUByDomain(message.getFrom().getDomain())
          	if(ouByDomain) {
              attrs.client = ouByDomain
            }
          	// Имя контактного лица заполнить именем отправителя
			attrs.clientName = message.from.name
			// Контактный email заполнить email-ом отправителя
			attrs.clientEmail = message.from.address
		}
      	// Если контрагент не определён и не разрешена регистрация
      	else
        {
          	throw new ScriptError(ErrorType.UNKNOWN_ADDRESS, "Сотрудник с адресом ${message.from.address} не найден в системе. Регистрация заявок с неизвестных адресов запрещена")
        }
		
		// Определить соглашение для контрагента
		attrs.agreement = getAgreement(attrs.client, AGREEMENT_INVENTORY_NUMBER)
		
		// Определить услугу для соглашения
		attrs.service = getService(attrs.agreement, SERVICE_INVENTORY_NUMBER)

		// Определить тип заявки
		def metaClass = getMetaClass(SENDER_2_TYPE_CODE, message.from.address, DEFAULT_TYPE_CODE)
		
		// Определить часовой пояс заявки из значения по умолчанию в типе заявки
		attrs.timeZone = metaClass.getAttribute('timeZone').defaultValue ?: utils.get('root$101').timeZone
		
		// Если часовой пояс не определен
		if (!attrs.timeZone)
		{
			throw new ScriptError(ErrorType.TIMEZONE_NOT_DEFINED, "Ошибка! Для типа заявки \"${metaClass.title}\" не определен часовой пояс по умолчанию")
		}
		
      	// Тема и описание заявки
      	attrs.shortDescr = message.subject
      	attrs.descriptionRTF = message.getHtmlBody()
      	if (api.string.isEmptyTrim(attrs.descriptionRTF))
      	{
          	attrs.descriptionRTF = '[Описание не заполнено]'
        }
      	if (api.string.isEmptyTrim(attrs.shortDescr))
      	{
          	attrs.shortDescr = '[Тема не заполнена]'
		}
		if (attrs.shortDescr.size() > 255)
      	{
          	log('Тема входящего письма не удовлетворяет ограничениям по длине')
			attrs.shortDescr = '[Тема в описании]'
			attrs.descriptionRTF = """<b>Тема:</b> ${message.subject}<br/><b>Описание:</b><br>${message.getHtmlBody()}"""
        }
		
		// Определить уровень влияния заявки
		attrs.impact = 	findOne('impact', ['code' : IMPACT_CODE])
		
		if (attrs.impact == null)
		{
			log("Уровень влияния с кодом \"${IMPACT_CODE}\" не найден.")
		}
	
		// Определить уровень срочности заявки
		attrs.urgency = findOne('urgency', ['code' : URGENCY_CODE])
	
		if (attrs.urgency == null)
		{
			log("Уровень срочности с кодом \"${URGENCY_CODE}\" не найден.")
		}
	
		// Определить способ обращения
		attrs.wayAddressing = findOne('wayAdressing', ['code' : REQUEST_WAY_ELEMENT_CODE])
		
		if (attrs.wayAddressing == null)
		{
			log("Способ обращения с кодом \"${REQUEST_WAY_ELEMENT_CODE}\" не найден.")
		}
		
		// Создать заявку с указанными атрибутами
		log('Регистрация заявки...')
		try
		{
			serviceCall = utils.create(metaClass.code, attrs)
			notificationText = "по вашему обращению зарегистрирована заявка №${serviceCall.number}"
			log("Зарегистрирована заявка №${serviceCall.number}.")
		}
		catch (e)
		{
			throw new ScriptError(ErrorType.CREATE_SERVICECALL, "Ошибка! При создании заявки возникла ошибка: ${e.message}.")
		}
      
      	// Добавить в список файлов заявки вложения из письма
        def attachments = api.mail.attachMessageAttachments(serviceCall, message)
        log("К заявке добавлено вложенных файлов: ${attachments.size()}.")

		// Записать в результат обработки, что создан новый объект
		result.messageState = api.mail.NEW_BO_MSG_STATE
	}
	else // заявка найдена
	{
		// Добавить комментарий к найденной заявке
		log('Получение необходимых параметров комментария...')
		
		// Поиск автора по адресу отправителя
		def author = getEmployee(message.from.address, EMPLOYEE_TYPES, DEFAULT_EMPLOYEE_NAME)
      	if(author == null || author.lastName == DEFAULT_EMPLOYEE_NAME) {
          // Если автор комментария не определен или служебный
          if(allowedFromUnknownAddress) {
            log("Автор комментария не определён. Автором будет Суперпользователь")
          }
          else {
            throw new ScriptError(ErrorType.UNKNOWN_ADDRESS, "Сотрудник с адресом ${message.from.address} не найден в системе. Регистрация заявок с неизвестных адресов запрещена.")
          }
        }
       	else {
          log("Автор комментария ${author.title}")
        }
		
		// Получить часть тела письма до разделителя
		def text = getTextBeforeDelimiter([OUTGOING_COMMENT_DELIMITER] + OTHER_COMMENT_DELIMITERS, message.htmlBody)
		
		// Заменить все виды пробелов в тексте на "обычный" - ' '
		text = replaceAllWhitespaces(text)
		
		// Если текст комментария пуст
		if (api.string.isEmptyTrim(text))
		{
			text = "[Текст не заполнен]"
			log("Текст комментария не заполнен.")
		}
		
		// Добавить в текст комментария адрес отправителя письма
		text = text + "<br>Email отправителя: " + message.from.address
		
		// Добавление комментария с текстом письма
		log('Добавление комментария...')
      	def comment 
		
		// Создать комментарий и добавить его к заявке
		try
		{
			comment = utils.create('comment', ['source' : serviceCall.UUID, 'text' : text, 'author' : author])
			notificationText = "к заявке №${serviceCall.number} добавлен комментарий"
			log("К заявке №${serviceCall.number} добавлен комментарий.")
			
		}
		catch (e)
		{
			throw new ScriptError(ErrorType.CREATE_COMMENT, "Ошибка! При добавлении комментария возникла ошибка: ${e.message}.")
		}
			
		// Добавление файлов к комментарию
      	def attachments = api.mail.attachMessageAttachments(comment, message, COMMENT_FILE_ATTR)
			
		// Записать в результат обработки, что письмо было прикреплено к существующему объекту
		result.messageState = api.mail.ATTACH_MSG_STATE	
	}
	
	// При необходимости добавить в список файлов заявки письмо в формате eml
	if (ADD_ORIGINAL_MAIL)
	{
		try
		{
			api.mail.attachMessage(serviceCall, message)
			log('К заявке добавлен файл с содержимым письма.')
		}
		catch (e)
		{
			log("""При добавлении к заявке файла с содержимым письма произошла ошибка: ${e.message}.
Возможно, в параметрах задачи обработки входящей почты не установлен флажок \"Сохранять в системе исходное письмо после обработки\".""")			
		}
	}
	
	// Инициировать событие "Поступление письма"
	api.mail.notifyMailReceived(serviceCall)
}
catch (ScriptError e) // обработка предусмотренных ошибок (типы ошибок см. ниже)
{
	switch (e.type)
	{
		case ErrorType.REJECT:
			notificationText = "письмо отклонено по следующей причине: ${e.message}"
			log("Ошибка! Письмо отклонено по следующей причине: ${e.message}.")
			// Записать в результат обработки, что письмо было отклонено при первоначальной обработке
			result.messageState = api.mail.REJECT_MSG_STATE
			break
              
        case ErrorType.AUTO_MAIL:
      		notificationText = null // чтобы не отправлять оповещение
      		log('Автоматическое письмо. Не обрабатываем')
      		result.messageState = api.mail.ERROR_MSG_STATE
			break
			
		case ErrorType.SENDER_NOT_IDENTIFIED:
			notificationText = 'при обработке Вашего письма возникла ошибка: отправитель письма не определен'
			log(e.message)
			// Записать в результат обработки, что при обработке письма произошла ошибка
			result.messageState = api.mail.ERROR_MSG_STATE
			break
			
		case ErrorType.UNKNOWN_ADDRESS:
			notificationText = 'Пользователь с Вашим email-адресом не найден. Обработка писем с неизвестных адресов отключена'
			log(e.message)
			// Записать в результат обработки, что при обработке письма произошла ошибка
			result.messageState = api.mail.ERROR_MSG_STATE
			break
              
		case ErrorType.CREATE_COMMENT:
		case ErrorType.CREATE_SERVICECALL:
		case ErrorType.EMPLOYEE_BY_LASTNAME_NOT_FOUND:
		case ErrorType.AGREEMENT_FOR_EMPLOYEE_NOT_FOUND:
		case ErrorType.METACLASS_NOT_FOUND:
		case ErrorType.TIMEZONE_NOT_DEFINED:
			notificationText = 'при обработке Вашего письма возникла ошибка'
			log(e.message)
			// Записать в результат обработки, что при обработке письма произошла ошибка
			result.messageState = api.mail.ERROR_MSG_STATE
      		maybeError = e
			break
	}
}
catch (Exception e) // обработка непредусмотренных ошибок
{
	notificationText = 'при обработке Вашего письма возникла ошибка'
	log("В процессе обработки письма возникла ошибка: ${e.message}.")
	// Записать в результат обработки, что при обработке письма произошла ошибка
	result.messageState = api.mail.ERROR_MSG_STATE
  	maybeError = e
}

// Если установлен соответствующий параметр, и
// при обработке письма произошла ошибка,
// уведомить отправителя письма о результате обработки
if (NOTIFY_ABOUT_ERRORS && (result.messageState == api.mail.ERROR_MSG_STATE) && notificationText)
{
	sendResponse(message, notificationText, OUTGOING_COMMENT_DELIMITER)
}

// Если возникла ошибка
if(maybeError) {
  // Пробрасываем ошибку, чтоб откатить транзакцию
  throw(maybeError)
}

log('Обработка завершена.')
if(result.isError()) 
{
	result.error(mailLogMsg + "</ol>")
}


//ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ (Service Desk)---------------------------------------------------------------------
/**
 * Типы ошибок скрипта обработки
 */
enum ErrorType {
	REJECT, // письмо отклонено при первоначальной обработке
    AUTO_MAIL, //автоматическое письмо
	CREATE_SERVICECALL, // ошибка при добавлении заявки
	CREATE_COMMENT, // ошибка при добавлении комментария
	SENDER_NOT_IDENTIFIED, // отправитель письма не определен
	EMPLOYEE_BY_LASTNAME_NOT_FOUND, // сотрудник не найден
    UNKNOWN_ADDRESS, // письмо с неизвестного адреса
	AGREEMENT_FOR_EMPLOYEE_NOT_FOUND, // соглашение для сотрудника не найдено
	METACLASS_NOT_FOUND, // тип заявки с указанным кодом не существует
	TIMEZONE_NOT_DEFINED // для типа заявки не указан часовой пояс по умолчанию
}

/**
 * Класс ошибок скрипта обработки
 */
class ScriptError extends Exception
{
	ErrorType type
		
	ScriptError(ErrorType type, String message)
	{
		super(message)
		this.type = type
	}
	
	ScriptError(ErrorType type)
	{
		this(type, null)
	}
}

/**
 * Функция записи сообщения в лог
 */
def log(msg)
{
	def logLevels = ['error', 'warn', 'info', 'debug', 'trace']
	
	if (LOGGING_IS_ENABLED)
	{
		if (logLevels.contains(LOG_LEVEL))
		{
			logger."${LOG_LEVEL}"("[Обработка почты] ${msg}")
		}
		else
		{
			logger.error("[Обработка почты] Установлен некорректный уровень логирования \"${LOG_LEVEL}\"")
		}
      	mailLogMsg += "<li>[Обработка почты] ${msg}</li>"
	}
}

/**
 * Выполняет поиск объекта указанного метакласса по указанным значениям атрибутов.
 * Если не найдено ни одного объекта или найдено несколько объектов, возвращает null
 */
def findOne(fqn, attrs)
{
	try
	{
		return utils.get(fqn, attrs)
	}
	catch (e)
	{
		return null
	}
}

/**
 * Получение сотрудника одного из указанных типов по email-адресу
 */
def getEmployee(email, employeeTypes, defaultEmployeeName)
{
	if (!employeeTypes.empty)
	{
		log("Поиск сотрудника типов с кодами ${formatList(employeeTypes, '\"')} по адресу отправителя ${email}...")
	}
	else
	{
		log("Поиск сотрудника по адресу отправителя ${email}...")
	}
	
	// Поиск сотрудников по адресу отправителя письма
	def employees = api.mail.helper.searchEmployeesByEmail(email).findAll{ !it.removed }
	
	// Выбор подходящего по типу сотрудника
	def applicableEmployees
	
	// Если набор допустимых типов сотрудников непуст
	if (!employeeTypes.empty)
	{
		// Отфильтровать найденных сотрудников по типу
		applicableEmployees = employees.findAll {
			employeeTypes.contains(it.getMetainfo().getCase())
		}
	}
	else
	{
		applicableEmployees = employees
	}
	
	// Результат работы (устанавливается далее)
	def res = null
	
	switch (applicableEmployees.size())
	{
		// Не найдено ни одного подходящего сотрудника
		case 0:
			log('Сотрудник не найден.')
			break
			
		// Найден ровно один подходящий сотрудник
		case 1:
			res = applicableEmployees.first()
			log("Найден сотрудник: ${res.title}.")
			break
		
		// Найдено несколько подходящих сотрудников
		default:
			log("Найдено более одного сотрудника: ${formatList(applicableEmployees*.title, '')}.")
	}
	
	// Если сотрудник по email-адресу и типу не определен
	if (!res)
	{
		// Получить служебного сотрудника по фамилии
		res = getEmployeeByLastName(defaultEmployeeName)
	}
	
	return res
}

/**
 * Получение сотрудника по фамилии
 */
def getEmployeeByLastName(lastName)
{
	log("Поиск сотрудника по фамилии \"${lastName}\"...")
	
	// Результат работы (устанавливается далее)
	def res = null
	def employees = utils.find('employee', ['lastName': lastName, 'removed' : false])

	switch (employees.size())
	{
		// Не найдено ни одного сотрудника
		case 0:
			log("Не найдено ни одного сотрудника с фамилией \"${lastName}\".")
      		break
		
		// Найден один сотрудник
		case 1:
			res = employees.first()
			log("По фамилии найден сотрудник: ${res.title}.")
			break
			
		// Найдено несколько сотрудников
		default:
			throw new ScriptError(ErrorType.EMPLOYEE_BY_LASTNAME_NOT_FOUND,
				"Ошибка! Найдено более одного сотрудника с фамилией \"${lastName}\": ${formatList(employees*.title, '')}.")
	}

	return res
}

/**
 * Получение компании по домену отправителя
 */
def getOUByDomain(domain)
{
  	log("Поиск компании по домену отправителя \"${domain}\"...")
  
  	def res = null
  	def domainObj = utils.find('domain', ['title' : domain, 'removed' : false])
  	
  	// Так как название уникальное, то если найтись может не более одного домена
  	if(domainObj)
  	{
      	// Получаем компанию из первого домена
    	res = domainObj[0].company
      	log("Домен найден. По домену получили компанию ${res?.title}")
    }
  
  	return res
}

/**
 * Получение соглашения для сотрудника
 */
def getAgreement(employee, inventoryNumber)
{
  // Результат работы (устанавливается далее)
  def res = null
  if(employee)
  {
    log("Поиск соглашения для сотрудника ${employee.title}...")

    def agreements = employee.recipientAgreements.findAll{ !it.removed }

    switch (agreements.size())
    {
      // У сотрудника нет ни одного соглашения
      case 0:
      	throw new ScriptError(ErrorType.AGREEMENT_FOR_EMPLOYEE_NOT_FOUND,
                            "Ошибка! Сотрудник не является получателем ни одного соглашения.")

      // У сотрудника одно соглашение
      case 1:
      	res = agreements.first()
      	log("Сотрудник получает соглашение: \"${res.title}\".")
      	break;

      // У сотрудника несколько соглашений
      default:
        log("Сотрудник получает более одного соглашения: ${formatList(agreements*.title, '\"')}.")
      	// Если среди соглашений сотрудника есть соглашение с указанным уникальным номером,
      	// установить данное соглашение в качестве результата
      	res = agreements.find { it.inventoryNumber == inventoryNumber }
      	if(res)
      	{
          log("Для сотрудника определено соглашение: \"${res.title}\".")
        }
      	else
        {
          res = agreements.first()
          log("Базового нет, выбрали случайное - \"${res.title}\".")
          //throw new ScriptError(ErrorType.AGREEMENT_FOR_EMPLOYEE_NOT_FOUND, "Ошибка! Сотрудник получает несколько соглашений, но среди них нет соглашения с уникальным номером \"${inventoryNumber}\".")
        }
    }
  }
  else 
  {
    log("Поиск соглашения по умолчанию...")
    def agreements = utils.find('agreement', ['inventoryNumber' : inventoryNumber, 'removed' : false])

    //Не найдено ни одного соглашения по уникальному номеру
    if (agreements.size() == 0)
    {
      throw new ScriptError(ErrorType.AGREEMENT_FOR_EMPLOYEE_NOT_FOUND,
                            "Ошибка! Не найдено ни одного соглашения с уникальным номером ${inventoryNumber}")
    }
    res = agreements.first()
    log("Найдено соглашение: \"${res.title}\".")
  }

  return res
}

/**
 * Получение услуги по соглашению
 */
def getService(agreement, inventoryNumber)
{
	log("Поиск услуги для соглашения \"${agreement.title}\"...")
	
	// Результат работы (устанавливается далее)
	def res = null
	def services = agreement.services.findAll{ !it.removed }
	
	switch (services.size())
	{
		// С соглашением не связано ни одной услуги
		case 0:
			log('С соглашением не связано ни одной услуги.')
			break
		
		// С соглашением связана одна услуга
		case 1:
			res = services.first()
			log("С соглашением связана услуга: \"${res.title}\".")
			break
			
		// С соглашением связано несколько услуг
		default:
			log("С соглашением связаны услуги: ${formatList(services*.title, '\"')}.")
			
			// Если среди услуг соглашения есть услуга с указанным уникальным номером,
			// установить данную услугу в качестве результата
			for (service in services)
			{
				if (service.inventoryNumber == inventoryNumber)
				{
					res = service
					log("По уникальному номеру \"${inventoryNumber}\" определена услуга: \"${res.title}\".")
					break
				}
			}
			
			// Если соглашение не связано с услугой с указанным уникальным номером
			if (!res)
			{
				log("С соглашением связано несколько услуг, но среди них нет услуги с уникальным номером \"${inventoryNumber}\". Услуга не заполняется.")
			}
	}
	
	return res
}

/**
 *  Получение типа запроса по email сотрудника (или из значения по умолчанию)
 */
def getMetaClass(sender2TypeCode, email, defaultTypeCode)
{
	def typeCode = sender2TypeCode[email]
	
	// Если тип запроса по email определен
	if (typeCode)
	{
		log("По адресу отправителя ${email} найден тип заявки с кодом \"${typeCode}\".")
	}
	else
	{
		log("Тип заявки по адресу отправителя ${email} не найден, используется тип заявки с кодом \"${defaultTypeCode}\".")
		typeCode = defaultTypeCode
	}
	
	def metaClassCode = 'serviceCall$' + typeCode
	
	// Если существует тип с данным кодом
	if (api.metainfo.metaClassExists(metaClassCode))
	{
		return api.metainfo.getMetaClass(metaClassCode)
	}
	
	throw new ScriptError(ErrorType.METACLASS_NOT_FOUND, "Ошибка! Тип заявки с кодом \"${typeCode}\" не существует.")
}

/**
 * Отправляет ответ на письмо с указанным текстом
 */
def sendResponse(msg, responseText, delimiter)
{
	def address = msg.from.address
	
	// Если адрес получателя совпадает с адресом исходящей почты системы
	if (api.mail.helper.isSystemEmailAddress(address))
	{
		log("Оповещение на адрес ${address} не отправлено, т.к. данный адрес совпадает с адресом системы.")
		return
	}
	
	def name = msg.from.name
	def subject = 'Re: ' + msg.subject
	
	// Определить приветствие, добавляемое в текст оповещения
	def greeting = !api.string.isEmptyTrim(name) ? name: 'пользователь'
	
	// Сформировать текст оповещения из разделителя, приветствия, сообщения и цитаты оригинального письма
	def body = """${delimiter}

Уважаемый(ая) ${greeting}, ${responseText}.

${api.mail.respondBody(msg)}"""
		
	try
	{
		api.mail.simpleSender.send(address, name, subject, body)
		log("На адрес ${address} отправлено оповещение.")
	}
	catch (e)
	{
		log("При отправке оповещения на адрес ${address} возникла ошибка: ${e.message}. Возможно, недоступен сервер исходящей почты, или включен режим \"SILENT MODE\".")
	}
}

/**
 * Определяем, автоматическое письмо или нет
 */
def isAutoAnswerByMessage(message) {
  def result = false
  // сначала проверяем заголовки
  def headers = message.getHeaders()

  def isByHeaders = [
    (headers.get('X-autorespond') != null),
    (headers.get('Precedence') in ['auto_reply', 'bulk', 'junk']),
    (headers.get('X-Precedence') in ['auto_reply', 'bulk', 'junk']),
    (headers.get("Auto-Submitted") == 'auto-replied')
  ]

  if(isByHeaders.any{ it == true }) {
    log('Автоответ по заголовкам')
    result = true
  } else {
    // проверяем тему
    def autoThemeStarts = utils.find('mailAutoSubj', ['removed' : false]).title
    def mesSubj = message.subject
    if(mesSubj && autoThemeStarts.any { mesSubj.startsWith(it) }) {
      log('Автоответ по теме')
      result = true
    }
  }
  return result 
}

//ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ (общего назначения)----------------------------------------------------------------
/**
 * В заданной строке (line) выполняет поиск числа после заданного префикса (prefixes)
 * Если список префиксов пуст, выполняется поиск числа без учета префикса
 */
def getNumberAfterPrefix(prefixes, line)
{
	def newPrefixes = !prefixes.empty ? prefixes: ['']
	
	for (prefix in newPrefixes)
	{
		// Создать шаблон регулярного выражения для строки prefix
		def pattern = Pattern.quote(prefix) + '(\\d+)'

		def matcher = line =~ /${pattern}/
		
		// Если строка соответствует шаблону
		if (matcher)
		{
			// Вернуть найденное после префикса число
			return matcher[0][1]
		}
	}
	
	return null
}

/**
 * Возвращает часть текста от его начала до ближайшего к началу текста разделителя
 * (или весь текст, если разделитель в тексте не найден)
 */
def getTextBeforeDelimiter(delimiters, text)
{
	def minPosition = null
	
	for (delimiter in delimiters)
	{
		def position = text.indexOf(delimiter)
		
		if ((position > 0) && (position < minPosition || minPosition == null))
		{
			minPosition = position
		}
	}
	
	return minPosition != null ? text.substring(0, minPosition): text
}

/**
 * Форматирует список в строку (для элементов списка указываются обрамляющие кавычки)
 */
def formatList(l, quote)
{
	def separator = ', '
	def quotedList = l.collect {quote+it+quote}
	
	return quotedList.join(separator)
}

/**
 * Замена всех unicode пробелов на стандартный \u0020
 * http://www.cs.tut.fi/~jkorpela/chars/spaces.html
 */
def replaceAllWhitespaces(String str)
{/*&nbsp;|*/
	return str.replaceAll('(\u00A0|\u2000|\u2001|\u2002|\u2003|\u2004|\u2005|\u2006|\u2007|\u2008|\u2009|\u200A|\u200B|\u202F|\u205F|\u3000|\uFEFF)', ' ')
}