/*! UTF8 */
/*& 3600cache */
//Автор: mdemyanov
//Дата создания: 27.01.17
//Код: SCRIPTSD4001431
//Назначение: 
/**
 * Создание задачи из пользовательского действия:
 * @ATTR_CODES - перечень атрибутов для заполнения в создаваемой задаче
 * @FQN - код метакласса создаваемой задачи
 * @ERROR_MSG - сообщение об ошибки
 * @ERROR_DSC - описание ошибки
 * @FILES - код атрибута, в котром хранятся приложенные файлы
 * @MATCHING_ATTRS - ассоциативный массив для реализации соспоставления атрибутов,
 * отличных от описанных в классе задачи.
 * Сценарий формирует перечень параметров для создания задачи,
 * определяет код класса объекта, откуда производится действие 
 * для заполнения соответствующей ссылки и пытается создать новую задачу.
 * В случае возникновения ошибки пользоваетль увидит соответсвующее уведомление,
 * кроме того, будет сформированно сообщение в лог с полным стеком ошибки.
 */
//Версия: 4.4.x
//Категория: действие по событию

// Логгирование:
/*По умолчанию логгирование выключено*/
void log(message)
{
	def LOGGING_IS_ENABLED = true
	def MESSAGE = 'Создание задачи из пользовательского действия: '
	if (LOGGING_IS_ENABLED)
    {
		logger.error(MESSAGE + message.toString())
	}
}

void log(message, e)
{
	def LOGGING_IS_ENABLED = true
	def MESSAGE = 'Создание задачи из пользовательского действия: '
	if (LOGGING_IS_ENABLED)
    {
		logger.error(MESSAGE + message.toString(), e)
	}
}

//Параметры------------------------------------------------------
def FQN = 'task$task'
def ATTR_CODES = ['templateTask', 'responsible', 'planDate', 'deadline', 'description']
//TODO: обходное решение для бага NSDPRD-6114. После устранения очистить содержимое 
// или избавиться от использования
def MATCHING_ATTRS = [
        'responsible': 'resp'
]
def FILES = 'files'
def MESSAGE_IN_PAST = '"Измените дедлайн: Дедлайн указан в прошлом"'
def ERROR_MSG = 'Ошибка создания задачи'
def ERROR_DSC = '''

<p>При создании задачи возникла непредвиденная ошибка!
<p>Пожалуйста, сообщите об этом администратору системы.</p>'''

//Функции--------------------------------------------------------

//Основной блок -------------------------------------------------
def date = new Date()
def attrs = [:]
def task
ATTR_CODES.each {attr->
	attrs.put(attr, params[MATCHING_ATTRS[attr] ?: attr])
}
def cardClass = cardObject.metaClass.toString().split(/\$/)[0]
attrs.put(cardClass, cardObject)
attrs.put('author', user)
try
{
	api.tx.call{
		task = utils.create(FQN, attrs)
	}
} catch (Exception e) {
	log("возникла ошибка при попытке создать задачу в объекте ${cardObject.UUID}", e)
	//result.showMessage(ERROR_MSG, ERROR_DSC)
  utils.throwReadableException(ERROR_DSC, [] as String[], ERROR_DSC, [] as String[])
}