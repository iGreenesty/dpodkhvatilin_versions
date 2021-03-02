/*! UTF8 */
//Автор: mdemyanov
//Дата создания: 14.06.2018
//Код: SCRIPTSD4001926
//Назначение: 
/**
 * Скриптовый модуль для работы с обучающими турами. Умеет:
 * @method search найти туры и подготовить их для отображения пользователю
 * @method markAsDisabled пометить тур как отключенный для пользователя
 * @method markAsSuspended пометить тур как приостановленный для пользователя
 * @param ENABLED_LOGGING управление логирование
 * @param ENABLED_ERR управление логированием ошибок
 * @param MSG строка детализации текста сообщения в консоли
 * @param INTERFACE_TOUR_CLASS класс объектов с реализацией тура
 * @param SEC_GROUPS_ATTRIBUTE атрибут с группами прав
 * @param DISABLED_USERS_ATTRIBUTE атрибут с пользователями, у которых отключен тур
 * @param DISABLED_SUPER_USERS_ATTRIBUTE атрибут с суперпользователями, у которых отключен тур
 * @param SUSPENDED_USERS_ATTRIBUTE атрибут с пользователями, у которых приостановлен тур
 * @param SUSPENDED_SUPER_USERS_ATTRIBUTE атрибут с суперпользователями, у которых приостановлен тур
 * @param CASES_ATTRIBUTE атрибут с доступными типами
 * @param TAGS_ATTRIBUTE атрибут с доступными метками
 * @param CARD_CLASS_ATTRIBUTE атрибут с видом карточки
 * @param START_HASH_ATTRIBUTE атрибут со ссылкой на кокнретный объект, по которому доступен тур
 * @param SMP_ATTRIBUTES коллекция атрибутов, которые можно инициализировать прямо в туре из объекта SMP
 * @param LANGUAGE_ATTRIBUTE язык тура
 * @param HASH_BEGINNIG_WINDOW код карточки объекта
 * @param HASH_BEGINNIG_ADD код формы добавления объекта
 * @param HASH_BEGINNIG_EDIT код формы редактирования объекта
 * @author apopov , mdemyanov
 */
//Версия: 4.7.6.*
//Категория: 
//Параметры------------------------------------------------------
import groovy.transform.Field

@Field static boolean ENABLED_LOGGING = false
@Field static boolean ENABLED_ERR = true
@Field static String MSG = 'Модуль подготовки туров: '


@Field static String INTERFACE_TOUR_CLASS = 'interfaceTour'
@Field static String JSON_SETTINGS_ATTRIBUTE = 'jsonSettings'
@Field static String SEC_GROUPS_ATTRIBUTE = 'secGroups'
@Field static String PROFILE_ATTRIBUTE = 'profiles'
@Field static String DISABLED_USERS_ATTRIBUTE = 'disabledUsers'
@Field static String DISABLED_SUPER_USERS_ATTRIBUTE = 'disabledSuper'
@Field static String SUSPENDED_USERS_ATTRIBUTE = 'suspendedUsers'
@Field static String SUSPENDED_SUPER_USERS_ATTRIBUTE = 'suspendedSuper'
@Field static String CASES_ATTRIBUTE = 'cases'
@Field static String TAGS_ATTRIBUTE = 'tags'

@Field static String CARD_CLASS_ATTRIBUTE = 'cardClass'
@Field static String START_HASH_ATTRIBUTE = 'startHash'
@Field static List<String> SMP_ATTRIBUTES = [
		'buttonBack',
		'buttonNext',
		'buttonEnd',
		'buttonLater',
		'buttonNever',
		'UUID',
		'title',
		'required'
]
@Field static String LANGUAGE_ATTRIBUTE = 'userLanguage'
@Field static String HASH_BEGINNIG_WINDOW = 'uuid'
@Field static String HASH_BEGINNIG_ADD = 'add'
@Field static String HASH_BEGINNIG_EDIT = 'edit'

/*
 * Интерфейсный тур
 */

class Tour {
	String UUID
	String title
	String buttonBack
	String buttonNext
	String buttonEnd
	String buttonLater
	String buttonNever
	Boolean required = false
	Boolean disabled = false
	Boolean suspended = false
	List<Step> steps

	Tour(def tourObject, List<String> attrs) {
		attrs.each { attr ->
			this[attr] = tourObject[attr]
		}
	}
}
/*
 * Шаг тура
 */

class Step {
	/*
     * Вкладка, на которой отображается шаг
     */
	String tab
	/*
     * Наименование шага
     */
	String title
	/*
     * подсвечиваемый элемент
     */
	String element
	/*
     * Текст подсказки
     */
	String intro
	/*
     * Позиция подсказки
     */
	String position
	/*
     * Требуемые теги
     */
	String tags
	/*
     * Требуемые теги
     */
	String secGroups

	/**
	 * Проверить шаг на соответствие включенным меткам
	 * @param enabledTags - доступные включенные теги
	 * @return возвращает правду, если метки для шага пересекаются с достуными метками, либо метки для шага не определены
	 */
	Boolean checkTags(List<String> enabledTags) {
		if (tags) {
			List<String> tagList = tags.split(/,\s?/)
			return tagList.intersect(enabledTags)?.size() > 0
		}
		return true
	}
	/**
	 * Проверить шаг на соответствие доступным группам
	 * @param employeeSecGroups - доступные пользователю группы
	 * @return возвращает правду, если группы для шага пересекаются с достуными пользоваетлю грппами, либо группы для шага не определены
	 */
	Boolean checkSecGroups(List<String> employeeSecGroups) {
		if (secGroups) {
			List<String> tagList = secGroups.split(/,\s?/)
			return tagList.intersect(employeeSecGroups)?.size() > 0
		}
		return true
	}
	/**
	 * Получить коллекцию шагов из текста
	 * @param stepsText - текст с шагами тура в JSON
	 * @param enabledTags - коллекция доступных меток
	 * @return возвращает коллекцию шагов тура с учетом доступных меток
	 */
	static List<Step> getSteps(String stepsText,
							   List<String> enabledTags = null,
							   List<String> employeeSecGroups = null) {
		List<Step> steps = new com.google.gson.GsonBuilder()
				.serializeNulls()
				.create()
				.fromJson(stepsText, Step[].class) as List<Step>
		if (enabledTags) {
			steps = steps.findAll { it.checkTags(enabledTags) }
		}
		if (employeeSecGroups) {
			steps = steps.findAll { it.checkSecGroups(employeeSecGroups) }
		}
		return steps
	}
}
//Функции--------------------------------------------------------
/**
 * Логирование событий. Принимает на вход:
 * @param msg - сообщение для вывода в лог
 * @return Функция ничего не возвращет, но выводит сообщение в лог,<br>
 * в случае если логгирование включено.
 */
void log(msg) {
	if (ENABLED_LOGGING) {
		logger.info(MSG + msg.toString())
	}
}
/**
 * Логирование событий. Принимает на вход:
 * @param msg - сообщение для вывода в лог
 * @param e ошибка (может быть null
 * @return Функция ничего не возвращет, но выводит сообщение в лог ошибок, <br>
 * в случае если логгирование включено.
 */
void err(msg, e = null) {
	if (ENABLED_ERR) {
		logger.error(MSG + msg.toString(), e)
	}
}
/**
 * Получить значение атрибута
 * @param sourceObject объект
 * @param attrCode код атрибута
 * @return значение атрибута объекта или null (если атрибута нет или он пуст)
 */
def getAttrValue(sourceObject, attrCode) {
	if (utils.containsAttribute(sourceObject, attrCode)) {
		return sourceObject[attrCode]
	}
	return null
}
/**
 * Получить текущий объект или fqn
 * @param startHash
 * @return текущий объект или fqn
 */
def getObject(List<String> hashParts) {
	String startHash = hashParts[1]
	if ((startHash =~ /\w+\$\d/).find()) {
		return utils.get(startHash?.split('!')[0])
	}
	return startHash
}
/**
 * Проверить наличие пользователя в коллекции (например, отключенные или приостановленные туры)
 * @param attrs - коллекция кодов атрибутов тура для проверки пользователя
 * @param user - текущий пользователь или логин суперпользователя
 * @return правду , если находит пользователя в коллекции
 */
Boolean checkUser(List<String> attrs, object, user) {
	if (user instanceof String) {
		return object[attrs[0]]?.split(/,\s?/)?.contains(user)
	} else {
		return object[attrs[1]]?.contains(user)
	}
}
/**
 * Проверить доступность текущего тура по списку доступных пользователю групп
 * @param secGroupsAttr - код атрибута тура с группами
 * @param user - текущий пользователь
 * @return Closure ( tourObject ) , которая возвращает true, если в туре не указаны метки,
 * либо метки тура пересекаются с включенными метками
 */
Closure checkSecGroups(String secGroupsAttr, user) {
	{ tourObject ->
		if (user) {
			def groups = getSecGroups(tourObject[secGroupsAttr])
			if (groups) {
				return api.security.getAllEmployees(groups).contains(user.UUID)
			}
			return true
		}
		return true
	}
}
/**
 * Проверить доступность текущего тура по списку доступных пользователю профилей
 * @param profilesAttr - код атрибута тура с группами
 * @param user - текущий пользователь
 * @return Closure ( tourObject ) , которая возвращает true, если профили пользователя относительно объекта совпадают
 * с указанными в туре (либо в туре не отмечено кодов профилей)
 */
Closure checkProfile(String profilesAttr, user, object) {
	{ tourObject ->
		if (user != null && object != null) {
			def profileSettings = getAttrValue(tourObject, profilesAttr)
			List profiles = profileSettings ? profileSettings.split(/,\s?/) : null
			if (profiles) {
				return profiles.any { profile -> api.security.hasProfile(object, profile) }
			}
			return true
		}
		return true
	}
}
/**
 * Получить все группы прав доступа из строки
 * @param secGroups - строка с кодами групп
 * @return коллекция найденных групп или пустой массив
 */
def getSecGroups(String secGroups) {
	if (secGroups) {
		def groupCodes = []
		groupCodes += secGroups?.split(/,\s?/) as List
		return groupCodes?.findResults { it ? api.security.getGroup(it) : null }
	}
	return []
}
/**
 * Проверить все туры для текущего класса без принадлежности к типу и конкретной карточке
 * @param fqn - код метакласса
 * @param filter - базовые параметры поиска
 * @param cases - код класса/типа
 * @return коллекция найденных туров или пустой массив
 */
def findClassTour(String fqn, Map filter) {
	Map attrs = filter.clone()

	attrs[CASES_ATTRIBUTE] = op.isNull()
	attrs[START_HASH_ATTRIBUTE] = op.isNull()
	return utils.find(fqn, attrs)
}
/**
 * Проверить все туры для текущего типа без принадлежности к кокнретной карточке
 * @param fqn - код метакласса
 * @param filter - базовые параметры поиска
 * @param cases - код класса/типа
 * @return коллекция найденных туров или пустой массив
 */
def findCaseTour(String fqn, Map filter, cases) {
	if (cases.split(/\$/).size() == 2) {
		Map attrs = filter.clone()
		attrs[CASES_ATTRIBUTE] = api.types.newClassFqn(cases)
		attrs[START_HASH_ATTRIBUTE] = op.isNull()
		//log("поиск туров по кейсам, фильтр: ${attrs.inspect()}")
		return utils.find(fqn, attrs)
	}
	return []
}
/**
 * Проверить все туры для текущих типа без принадлежности к кокнретной карточке
 * @param fqn - код метакласса
 * @param filter - базовые параметры поиска
 * @param cases - коллекция кодов метаклассов
 * @return коллекция найденных туров или пустой массив
 */
def findCasesTour(String fqn, Map filter, List<String> cases) {
	def tours = []
	Map attrs = filter.clone()
	attrs[CASES_ATTRIBUTE] = cases.collect{caze-> api.types.newClassFqn(caze)}
	attrs[START_HASH_ATTRIBUTE] = op.isNull()
	//log("поиск туров по кейсам, фильтр: ${attrs.inspect()}")
	tours += utils.find(fqn, attrs)
	return tours.findAll {tour -> tour[CASES_ATTRIBUTE].containsAll(attrs[CASES_ATTRIBUTE])}
}
/**
 * Проверить все туры для текущего объекта
 * @param fqn - код метакласса
 * @param filter - базовые параметры поиска
 * @param startHash - строка с UUID объекта
 * @return коллекция найденных туров или пустой массив
 */
def findTour(String fqn, Map filter, startHash) {
	if ((startHash =~ /\w+\$\d/).find()) {
		Map attrs = filter.clone()
		attrs[START_HASH_ATTRIBUTE] = startHash?.split('!')[0]
		return utils.find(fqn, attrs)
	}
	return []
}
/**
 * Получить включенные метки
 * @return коллекция кодов включенных меток
 */
def getEnabledTags() {
	return beanFactory.getBean('tagServiceImpl').allTags.findResults { it.enabled ? it.code : null } as List<String>
}
/**
 * Проверить доступность текущего тура по списку доступных тегов
 * @param tagsAttrCode - код атрибута тура с тегами
 * @param enabledTags - коллекция кодов включенных меток
 * @return Closure ( tourObject ) , которая возвращает true, если в туре не указаны метки,
 * либо метки тура пересекаются с включенными метками
 */
Closure checkObjectTags(String tagsAttrCode, List<String> enabledTags) {
	{ tourObject ->
		if (utils.containsAttribute(tourObject, tagsAttrCode)) {
			def tags = tourObject[tagsAttrCode]?.split(/,\s?/) as List
			return tags?.size() ? tags.intersect(enabledTags).size() > 0 : true
		}
		return true
	}
}
/**
 * Возвращает доступные на текущей странице туры
 * @param windowHash - полный хэш текущей страницы (например, uuid:employee$8101, add:employee:ou$7901 или edit:employee$8101)
 * @param userLocale - язык пользователя
 */
def search(String windowHash, String userLocale, String superUserLogin) {
	log("windowHash $windowHash")
	List tours = []
	List result = []
	List<String> hashParts = getHashParts(windowHash)
	String cardClass = getСardClass(hashParts)
	//String objectCase = getObjectCase(hashParts)
	List<String> objectCases = getObjectCases(hashParts)
	String fqnCode = "$INTERFACE_TOUR_CLASS\$${objectCases[0].split(/\$/)[0]}"
	if (api.metainfo.metaClassExists(fqnCode) == false) {
		err("класс $fqnCode не существует")
		//Если для класса объекта тур не предусмотрен - просто ничего не ищем
		return []
	}
	Map filter = [removed: false]
	if (userLocale) {
		filter[LANGUAGE_ATTRIBUTE] = userLocale
	}
	filter[CARD_CLASS_ATTRIBUTE] = cardClass
	try {
		// найти все туры по классу, без учета туров под конкретные типы и карточки
		tours += findClassTour(fqnCode, filter)
		// найти все туры по типу, без учета туров под конкретные карточки
		//tours += findCaseTour(fqnCode, filter, objectCase)
		tours += findCasesTour(fqnCode, filter, objectCases)
		// найти все туры для карточки объекта
		tours += findTour(fqnCode, filter, hashParts[1])
	} catch (Exception e) {
		err("ошибки при поиске тура", e)
	}
	log("всего ${tours.size()}туров, фильтр: ${filter.inspect()}")
	result += tours
			.findAll(checkSecGroups(SEC_GROUPS_ATTRIBUTE, user)) // отфильтровать туры, недоступные по правам
			.findAll(checkProfile(PROFILE_ATTRIBUTE, user, getObject(hashParts))) // отфильтровать туры, недоступные по профилям
			.findAll(checkObjectTags(TAGS_ATTRIBUTE, getEnabledTags())) // отфильтровать туры, недоступные по меткам
			.findResults { tourObject ->
				def tour = new Tour(tourObject, SMP_ATTRIBUTES)
				tour.disabled = checkUser(
						[DISABLED_SUPER_USERS_ATTRIBUTE, DISABLED_USERS_ATTRIBUTE],
						tourObject,
						user ?: superUserLogin)
				tour.suspended = checkUser(
						[SUSPENDED_SUPER_USERS_ATTRIBUTE, SUSPENDED_USERS_ATTRIBUTE],
						tourObject,
						user ?: superUserLogin)
				try {
					tour.steps = Step.getSteps(tourObject[JSON_SETTINGS_ATTRIBUTE].text, getEnabledTags())
				} catch (Exception e) {
					tour = null
					err("ошибка подготовки шагов", e)
				}
				return tour
			}
	return result
}
/**
 * Получить содержимое hash текущего окна
 * @param windowHash - полный хэш текущей страницы (например, uuid:employee$8101, add:employee:ou$7901 или edit:employee$8101)
 * @return List < String >  коллекция частей хеша текущего окна, например, ['uuid', 'employee$8101']
 */
def getHashParts(String windowHash) {
	def hashParts = windowHash.replace(/!{"fast":"true"}/, '').split(/:(?!")/)

	if (!windowHash?.trim() && user) {
		hashParts = []
		hashParts.add(HASH_BEGINNIG_WINDOW)
		hashParts.add(user.UUID)
	} else if (hashParts.length < 2) {
		return []
	}
	return hashParts
}
/**
 * Получить класс/тип объекта текущего окна
 * @param windowHash - полный хэш текущей страницы (например, uuid:employee$8101, add:employee:ou$7901 или edit:employee$8101)
 * @return String тип текущего объекта
 */
def getObjectCase(List<String> hashParts) {
	String result = hashParts[1]
	if (api.string.contains(hashParts[1], '$') && !hashParts[0].equalsIgnoreCase(HASH_BEGINNIG_ADD)) {
		result = utils.get(hashParts[1].split('!')[0]).metaClass
	}
	return result
}
/**
 * Получить коллекцию типов объекта текущего окна
 * @param windowHash - полный хэш текущей страницы (например, uuid:employee$8101, add:employee:ou$7901 или edit:employee$8101)
 * @return String тип текущего объекта
 */
def getObjectCases(List<String> hashParts) {
	List<String> result = [hashParts[1]]
	if (api.string.contains(hashParts[1], '$') && !hashParts[0].equalsIgnoreCase(HASH_BEGINNIG_ADD)) {
		result = [utils.get(hashParts[1].split('!')[0]).metaClass.toString()]
	} else if (hashParts[3] != null && !api.string.contains(hashParts[1], '$') && hashParts[0].equalsIgnoreCase(HASH_BEGINNIG_ADD)) {
		result = hashParts[3].split(/,/).collect {caze-> "${hashParts[1]}\$${caze}"}
	}
	return result
}
/**
 * Получить тип текущей формы
 * @param windowHash - полный хэш текущей страницы (например, uuid:employee$8101, add:employee:ou$7901 или edit:employee$8101)
 * @return String тип текущей формы
 */
def getСardClass(List<String> hashParts) {
	def cardClass = 'undefined'
	if (hashParts[0].equalsIgnoreCase(HASH_BEGINNIG_WINDOW)) {
		cardClass = 'window'
	} else if (hashParts[0].equalsIgnoreCase(HASH_BEGINNIG_ADD)) {
		cardClass = 'newEntryForm'
	} else if (hashParts[0].equalsIgnoreCase(HASH_BEGINNIG_EDIT)) {
		cardClass = 'editForm'
	}
	return cardClass
}
/**
 * Помечает тур как отключенный для текущего пользователя
 * @param tourUuid - идентификатор тура
 * @param superUserLogin - логин суперпользователя или null, если запуск тура происходит для обычного пользователя
 */
def markAsDisabled(String tourUuid, String superUserLogin) {
	def tour = utils.get(tourUuid)
	if (user) {
		def valueArray = new java.util.ArrayList(tour[DISABLED_USERS_ATTRIBUTE])
		valueArray.add(user)
		def newValues = [:]
		newValues.put(DISABLED_USERS_ATTRIBUTE, valueArray)
		utils.edit(tour, newValues)
	} else if (superUserLogin) {
		def value = tour[DISABLED_SUPER_USERS_ATTRIBUTE]?.split(',').collect { it.trim() }
		if (!value) {
			value = []
		}
		if (!value.contains(superUserLogin)) {
			value.add(superUserLogin)
			def newValues = [:]
			newValues.put(DISABLED_SUPER_USERS_ATTRIBUTE, value.join(','))
			utils.edit(tour, newValues)
		}
	}
}

/**
 * Помечает тур как приостановленный для текущего пользователя
 * @param tourUuid - идентификатор тура
 * @param superUserLogin - логин суперпользователя или null, если запуск тура происходит для обычного пользователя
 */
def markAsSuspended(String tourUuid, String superUserLogin) {
	def tour = utils.get(tourUuid)
	if (user) {
		def valueArray = new java.util.ArrayList(tour[SUSPENDED_USERS_ATTRIBUTE])
		valueArray.add(user)
		def newValues = [:]
		newValues.put(SUSPENDED_USERS_ATTRIBUTE, valueArray)
		utils.edit(tour, newValues)
	} else if (superUserLogin) {
		def value = tour[SUSPENDED_SUPER_USERS_ATTRIBUTE]?.split(',').collect { it.trim() }
		if (!value) {
			value = []
		}
		if (!value.contains(superUserLogin)) {
			value.add(superUserLogin)
			def newValues = [:]
			newValues.put(SUSPENDED_SUPER_USERS_ATTRIBUTE, value.join(','))
			utils.edit(tour, newValues)
		}
	}
}
//Основной блок -------------------------------------------------