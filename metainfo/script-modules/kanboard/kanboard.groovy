/*! UTF8 */
//Автор: mdemyanov, vkuznetsov
//Дата создания: 17.01.2018
//Код: SCRIPTSD4001892
//Назначение:
/**
 * USER KANBAN MODULE:
 * Модуль и функции подготовки данных для KANBAN досок.
 * Готовые методы (используйте коды методов в качестве кодов контентов):
 * @func teamTasksByEmployees задачи команды в разрезе ответственных сотрудников
 * @func employeeTasksByStates задачи команды в разрезе статусов
 * @func teamServiceCallsByEmployees заявки команды в разрезе ответственных сотрудников
 * @func employeeServiceCallsByStates заявки команды в разрезе статусов
 * Вспомогтаельные методы:
 * @func createColumn создать представление колонки в формате MAP
 * @func createBoard создать представление доски в формате JSON
 */
//Версия: 4.8.*
//Категория:
//Параметры------------------------------------------------------
import groovy.json.JsonOutput
import groovy.transform.Field

@Field static boolean ENABLED_LOGGING = true
@Field static boolean ENABLED_ERR = true
@Field static String MSG = 'Модуль представлений для Kanban: '

@Field static Map SCALL_STATES = [
		registered      : 'Новое',
		inprogress      : 'В работе',
		waitClientAnswer: 'Ожидает ответа клиента',
		deferred        : 'Отложено',
		resumed         : 'Возобновлено'
]
@Field static String RESPONSIBLE = 'responsible'
@Field static String PRIORITY = 'priority'
@Field static String SCALL_DEADLINE_TIMER = 'timeAllowanceTimer'
@Field static String SCALL_DESCRIPTION = 'shortDescr'
@Field static String SCALL_TITLE = 'title'



@Field static String WHATSHOT = '''data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb
2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAZBJREFUeNpiYCADPLU0NSBWLRM5FnCbcTkALZlP
Mwv4PfgusCuwJRBrCVng5wz3/0ALQLif6j4AATZ5NgZmAWYQswBoiQPJFvzf7hsAxA74LOFQ54Ax55N
kAdBgAagmfzzyDMB4gAkpAH2RQIoPQOEKMiQBZhgaKAARrOIsyGL1RFkANRDmGhB7P3JQAdkFMMOgcY
DsC4z8wYLFUnSvGkAtwVD498NfdKF4IL5AKIjsiU1J///+v4DFMQTjwIFI8zewCLM4AukH+PRiCyIBP
IaCXHwAiBcyem6+AC2XQBYo4NLAQqRrQYYFAg19gEXuAz6NxFgAMsARaDhWg5i4mRj+ff1HUVFxAJfh
IAA0XIHUsugAsXECLYMM8OjFasFC9FQFzAMGOCqd9QT0MjDiKGvOw1z2+8UfhreL3n349+NfIVKS9Id
mSGTfXZA+ftqQ2EgO/Pv+7/lvl74LfD74BRZM8wkkhEBsEox4wlcBGgQGxCRhoOsfkGQBkkUJwKQYD0
wtDlgSw0KgwQsYhjUACDAAhuuE6pCuL88AAAAASUVORK5CYII='''
@Field static String TASK_ICON = '''data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb
2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAIpJREFUeNpiYBgFAw0YcUl8cTb7T4R+R569pw7g
U8BEoQMdaOqDb7/+Mbz89BtDXPfiRUaq+ICNhZGgGoosYGFiZGBipKEFEF8w0dYCTlYmWvuAcYhbQCi
iWXBJZP6F6Fp84CSG9sv6+g1Aqp4YB5DrgwNE+5Ac04E59QC+UoCqcTBqwSigPQAIMABtlxjKH6CoEQ
AAAABJRU5ErkJggg=='''
/**
 * Коды статусов счетчика.:
 * Кончился запас времени - 'e'
 * Активен - 'a'
 * Ожидает начала - 'n'
 * Приостановлен - 'p'
 * Остановлен - 's'
 */
@Field Map TIMER_STATES = [
		e: [
				color  : 'FF3300',
				title  : 'Просрочен',
				iconUrl: TASK_ICON
		],
		a: [
				color  : 'FFCC33',
				title  : 'Запущен',
				iconUrl: TASK_ICON
		],
		n: [
				color  : '00CC99',
				title  : 'Ожидает запуска',
				iconUrl: TASK_ICON
		],
		p: [
				color  : 'CCCC99',
				title  : 'Приостановлен',
				iconUrl: TASK_ICON
		],
		s: [
				color  : '999999',
				title  : 'Остановлен',
				iconUrl: TASK_ICON
		],
]
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
 * @return Функция ничего не возвращет, но выводит сообщение в лог ошибок, <br>
 * в случае если логгирование включено.
 */
void err(msg) {
	if (ENABLED_ERR) {
		logger.error(MSG + msg.toString())
	}
}
/**
 * Логирование событий. Принимает на вход:
 * @param msg - сообщение для вывода в лог
 * @param e - стек ошибки
 * @return Функция ничего не возвращет, но выводит сообщение в лог ошибок, <br>
 * в случае если логгирование включено.
 */
void err(msg, e) {
	if (ENABLED_ERR) {
		logger.error(MSG + msg.toString(), e)
	}
}
/**
 *
 */
def generateFileDownloadUrl(fileUuid) {
	"../operator/download?uuid=${fileUuid}"
}
/**
 * Получить крайний срок обратного счетчика. Принимает на вход:
 * @param object экземпляр объекта системы или ассоциативный массив
 * @param object
 * @return String значение счетчика (если определен, в противном случае - пустую строку)
 */
def getTimerDeadline(object, timer) {
	if (object[timer]) {
		return object[timer].deadLineTime
	} else {
		return ''
	}
}
/**
 * Получить ответственного в представлении для KANBAN. Принимает на вход:
 * @param object экземпляр объекта системы или ассоциативный массив
 * @param attrCode код атрбута ответственного (responsible)
 * @return Map массив с параметрами: title - наименование ответственного сотрудника или команды, <br>
 *     avatarUrl - ссылка на картинку пользователя (или пустая строка)
 */
def getAssignee(object, attrCode) {
	[
			title    : object[attrCode]?.title ?: 'Не указан',
			avatarUrl:
					object[attrCode]?.getMain()?.hasProperty('image') &&
							!object[attrCode].image.isEmpty() ?
							generateFileDownloadUrl(object[attrCode].image[0].UUID) :
							''
	]
}
/**
 * Получить элемент справочника в представлении для KANBAN. Принимает на вход:
 * @param object экземпляр объекта системы или ассоциативный массив
 * @param attrCode код атрибута справочника
 * @return Map массив с параметрами: title - наименование справочника, <br>
 *     color - цвет справочника (0099FF - по-умолчанию), <br>
 *         iconUrl - ссылка на иконку или представление по-молчанию в base64
 */
def getCategory(object, attrCode) {
	[
			color  : object[attrCode]?.color?.string ?: '0099FF',
			title  : object[attrCode]?.title ?: '',
			iconUrl: object[attrCode]?.icon?.size() ?
					generateFileDownloadUrl(object[attrCode]?.icon[0].UUID) : WHATSHOT
	]
}

/**
 * Получить элемент справочника в представлении для KANBAN.
 * Используется для определения просроченности задачи.
 * Принимает на вход:
 * @param object экземпляр объекта системы или ассоциативный массив
 * @param attrCode код атрибута справочника
 * @return Map массив с параметрами: title - наименование справочника, <br>
 *     color - цвет справочника (B2E8AC - по-умолчанию), <br>
 *         iconUrl - ссылка на иконку или представление по-молчанию в base64
 */
def getTaskOverdue(object, attrCode) {
  	[
      	color  : object[attrCode]?.color?.string ?: 'B2E8AC',
      	title  : object[attrCode]?.title ?: 'не просрочен',
      	iconUrl: object[attrCode]?.icon?.size() ?
					generateFileDownloadUrl(object[attrCode]?.icon[0].UUID) : TASK_ICON
    ]
}

/**
 * Форматирует карточку запроса объекта в представление для KANBAN. Принимает на вход:
 * @param id код атрибута идентификатора объекта
 * @param assigneeAttr код атрибута ответственного (агрегирующий)
 * @param categoryAttr код атрибута категории
 * @param deadlineTimer код атрибута обратного счетчика
 * @param description код атрибута описания
 * @param state код атрибута статуса
 * @param title код атрибута наименования
 * @param serviceCall экземпляр заявки для форматирования
 * @return Map форматированный массив для отрисовки карточки заявки на доске
 */
def asKanbanCard(Closure getId,
                 Closure getAssignee,
                 Closure getCategory,
                 Closure getDeadline,
                 Closure getDescription,
                 Closure getState,
                 Closure getTitle) {
	{ obj ->
		[
				id         : getId(obj),
				assignee   : getAssignee(obj),
				category   : getCategory(obj),
				deadline   : getDeadline(obj),
				description: getDescription(obj),
				status     : getState(obj),
				title      : getTitle(obj)
		]
	}
}
/**
 * Форматирует колонку с объектами для KANBAN. Принимает на вход:
 * @param fqn код метакласса объектов для поиска
 * @param sourceUUID уникальный идентификатор объекта привязки
 * @param sourceLink ссылка на атрибут объекта привязки
 * @param objCollector форматтер объектов для сбора в колонку
 * @return замыкание , которое принимает на вход правило поиска и возвращает колонку в виде HashMap
 */
def createColumn(fqn, sourceUUID, sourceLink, objCollector) {
	return { String objectUUID, Map settings ->
		// Получение объектов для доски
		def tasks = []
		def attrs = [:]
		attrs[sourceLink] = sourceUUID
		if (settings.containsKey('search')) {
			attrs.putAll(settings.search)
          	attrs.removed = false
		}
		tasks += utils.find(fqn, attrs, sp.limit(settings.limit ?: 100)) as List
		if (settings.containsKey('sorter')) {
			tasks = tasks.sort(params.sorter)
		}
		// Настрока колонки для отображения
		def column = [:]
		column.title = settings.title
		column.tasks = tasks.collect(objCollector) ?: []
		column.id = objectUUID
		column.statuses = settings.statuses
		return column
	}
}
/**
 * Форматирует json представление для доски KANBAN. Принимает на вход:
 * @param fqn код метакласса объектов для поиска
 * @param sourceUUID уникальный идентификатор объекта привязки
 * @param sourceLink ссылка на атрибут объекта привязки
 * @param objCollector форматтер объектов для сбора в колонку
 * @param data данные для поиска объектов
 * @param editing атрибут для редактирования при перетаскивании между колонками (false - если вызывается форма смены статуса)
 * @return json представление для доски KANBAN
 */
def createBoard(fqn, sourceUUID, sourceLink, objCollector, data, editing = false) {
	def view = [:]
	view.columns = data.collect(
			createColumn(fqn, sourceUUID, sourceLink, objCollector)
	)
	if (editing != false) {
		view.attributeCodeForEditing = editing
	}
	return JsonOutput.toJson(view)
}
//Основной блок -------------------------------------------------
/**
 * Замыкания, которые готовят данные для отрисовки:
 * <br> уникальный идентификатор объекта
 * <br> представление для отображения информации о контрагенте заявки
 * <br> представление для отображения информации о приоритете заявки
 * <br> представление для отображения информации о плановой дате решения заявки
 * <br> представление для отображения краткого описания заявки
 * <br> представление для отображения текущего статуса заявки
 * <br> представление для отображения названия заявки
 * @return ассоциативный массив с параметрами дл отрисовки карточки
 */
@Field Closure serviceCallForResponsibleEmployee = asKanbanCard(
		{ serviceCall -> serviceCall.UUID },
		{ serviceCall -> getAssignee(serviceCall, 'client') },
		{ serviceCall -> getCategory(serviceCall, 'priority') },
		{ serviceCall -> serviceCall.timeAllowanceTimer.deadLineTime },
		{ serviceCall -> serviceCall.shortDescr },
		{ serviceCall -> serviceCall.state },
		{ serviceCall -> "#${serviceCall.title}: ${serviceCall.service?.title ?: serviceCall.agreement?.title}" }
)
@Field Closure serviceCallForResponsibleEmployeeWithResp = asKanbanCard(
		{ serviceCall -> serviceCall.UUID },
		{ serviceCall -> getAssignee(serviceCall, 'responsible') },
		{ serviceCall -> getCategory(serviceCall, 'priority') },
		{ serviceCall -> serviceCall.timeAllowanceTimer.deadLineTime },
		{ serviceCall -> serviceCall.shortDescr },
		{ serviceCall -> serviceCall.state },
		{ serviceCall -> "#${serviceCall.title}: ${serviceCall.service?.title ?: serviceCall.agreement?.title}" }
)
@Field Closure taskForResponsibleEmployee = asKanbanCard(
		{ task -> task.UUID },
		{ task ->
			task.author ?
					[title    : task.author.title,
					 avatarUrl: !task.author.image.isEmpty() ?
							 generateFileDownloadUrl(task.author.image[0].UUID) : ''] : [title: 'Суперпользователь', avatarUrl: '']
		},
		//{ task -> TIMER_STATES[task.solveTimeRes.status.code] },
  		{ task -> getTaskOverdue(task, 'isOverdue') },
		{ task -> task.deadline },
		{ task -> (task.description) ?  (api.string.htmlToText(task.description).replaceAll(/&quot;/, '"')) : 'Описание не заполнено'  },
		{ task -> task.state },
		{ task -> "Задача: ${task.title}" }
)
@Field Closure taskForResponsibleEmployeeWithResp = asKanbanCard(
		{ task -> task.UUID },
		{ task -> getAssignee(task, 'responsible') },
		//{ task -> TIMER_STATES[task.solveTimeRes.status.code] },
  		{ task -> getTaskOverdue(task, 'isOverdue') },
		{ task -> task.deadline },
		{ task ->  (task.description) ?  (api.string.htmlToText(task.description).replaceAll(/&quot;/, '"')) : 'Описание не заполнено' },
		{ task -> task.state },
		{ task -> "Задача: ${task.title}" }
)
/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Заявки сотрудника в разрезе статусов. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */
def employeeServiceCallsByStates(sourceUUID, userUUID = null) {
	def states = [
			registered      : [
					title   : 'Новая',
					statuses: ['registered', 'resumed'],
					search  : [
							state: ['registered', 'resumed']
					]
			],
			inprogress      : [
					title   : 'В работе',
					statuses: ['inprogress'],
					search  : [
							state: ['inprogress']
					]
			],
			waitClientAnswer: [
					title   : 'Ожидает [клиента]',
					statuses: ['waitClientAnswer', 'deferred'],
					search  : [
							state: ['waitClientAnswer', 'deferred']
					]
			],
			resolved        : [
					title   : 'Решена',
					statuses: ['resolved'],
					search  : [
							state         : ['resolved'],
							stateStartTime: op.gt(new Date().clearTime())
					]
			]
	]
	return createBoard(
			'serviceCall',
			sourceUUID ?: userUUID,
			'responsibleEmployee',
			serviceCallForResponsibleEmployee,
			states
	)
}

/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Все заявки в разрезе статусов для карточки компании. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */
def rootServiceCallsByStates(sourceUUID, userUUID = null) {
	def states = [
			registered      : [
					title   : 'Новая',
					statuses: ['registered', 'resumed'],
					search  : [
							state: ['registered', 'resumed']
					]
			],
			inprogress      : [
					title   : 'В работе',
					statuses: ['inprogress'],
					search  : [
							state: ['inprogress']
					]
			],
			waitClientAnswer: [
					title   : 'Ожидает [клиента]',
					statuses: ['waitClientAnswer', 'deferred'],
					search  : [
							state: ['waitClientAnswer', 'deferred']
					]
			],
			resolved        : [
					title   : 'Решена',
					statuses: ['resolved'],
					search  : [
							state         : ['resolved'],
							stateStartTime: op.gt(new Date().clearTime())
					]
			]
	]
	return createBoard(
			'serviceCall',
			op.isNotNull(),
			'UUID',
			serviceCallForResponsibleEmployeeWithResp,
			states
	)
}

/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Заявки на команде в разрезе статусов. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */

def teamServiceCallsByStates(sourceUUID, userUUID = null) {
	def states = [
			registered      : [
					title   : 'Новая',
					statuses: ['registered', 'resumed'],
					search  : [
							state: ['registered', 'resumed']
					]
			],
			inprogress      : [
					title   : 'В работе',
					statuses: ['inprogress'],
					search  : [
							state: ['inprogress']
					]
			],
			waitClientAnswer: [
					title   : 'Ожидает [клиента]',
					statuses: ['waitClientAnswer', 'deferred'],
					search  : [
							state: ['waitClientAnswer', 'deferred']
					]
			],
			resolved        : [
					title   : 'Решена',
					statuses: ['resolved'],
					search  : [
							state         : ['resolved'],
							stateStartTime: op.gt(new Date().clearTime())
					]
			]
	]
	return createBoard(
			'serviceCall',
			sourceUUID,
			'responsibleTeam',
			serviceCallForResponsibleEmployeeWithResp,
			states
	)
}
/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Заявки на команде по сотрудникам. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */
def teamServiceCallsByEmployees(sourceUUID, userUUID = null) {
	def states = ['registered', 'resumed', 'inprogress', 'waitClientAnswer', 'deferred']
	def responsible = [:]
	responsible += [
			notResponsible: [
					title   : 'Без ответственного',
					statuses: [null],
					search  : [
							state              : states,
							responsibleEmployee: null
					]
			]
	]
	responsible += utils.get(sourceUUID).members.collectEntries {
		[
				(it.UUID): [
						title   : it.title,
						statuses: [it.UUID],
						search  : [
								state              : states,
								responsibleEmployee: it.UUID
						]
				]
		]
	}
	return createBoard(
			'serviceCall',
			sourceUUID,
			'responsibleTeam',
			serviceCallForResponsibleEmployee,
			responsible,
			'responsibleEmployee'
	)
}
/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Задачи сотрудника в разрезе статусов. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */
def employeeTasksByStates(sourceUUID, userUUID = null) {
	def states = [
			registered: [
					title   : 'Запланирована',
					statuses: ['registered', 'resumed'],
					search  : [
							state: ['registered', 'resumed']
					]
			],
			inprogress: [
					title   : 'В работе',
					statuses: ['inprogress'],
					search  : [
							state: ['inprogress']
					]
			],
			stopped   : [
					title   : 'Отложена',
					statuses: ['stopped'],
					search  : [
							state: ['stopped']
					]
			],
			closed    : [
					title   : 'Решена',
					statuses: ['resolved'],
					search  : [
							state         : ['resolved'],
							stateStartTime: op.gt(new Date().clearTime())
					]
			]
	]
	return createBoard(
			'task',
			sourceUUID ?: userUUID,
			'responsibleEmployee',
			taskForResponsibleEmployee,
			states
	)
}

/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Все задачи в разрезе статусов для карточки компании. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */

def rootTasksByStates(sourceUUID, userUUID = null) {
	def states = [
			registered: [
					title   : 'Запланирована',
					statuses: ['registered', 'resumed'],
					search  : [
							state: ['registered', 'resumed']
					]
			],
			inprogress: [
					title   : 'В работе',
					statuses: ['inprogress'],
					search  : [
							state: ['inprogress']
					]
			],
			stopped   : [
					title   : 'Отложена',
					statuses: ['stopped'],
					search  : [
							state: ['stopped']
					]
			],
			closed    : [
					title   : 'Решена',
					statuses: ['resolved'],
					search  : [
							state         : ['resolved'],
							stateStartTime: op.gt(new Date().clearTime())
					]
			]
	]
	return createBoard(
			'task',
			op.isNotNull(),
			'UUID',
			taskForResponsibleEmployeeWithResp,
			states
	)
}

/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Задачи на команде в разрезе статусов. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */

def teamTasksByStates(sourceUUID, userUUID = null) {
	def states = [
			registered: [
					title   : 'Запланирована',
					statuses: ['registered', 'resumed'],
					search  : [
							state: ['registered', 'resumed']
					]
			],
			inprogress: [
					title   : 'В работе',
					statuses: ['inprogress'],
					search  : [
							state: ['inprogress']
					]
			],
			stopped   : [
					title   : 'Отложена',
					statuses: ['stopped'],
					search  : [
							state: ['stopped']
					]
			],
			closed    : [
					title   : 'Решена',
					statuses: ['resolved'],
					search  : [
							state         : ['resolved'],
							stateStartTime: op.gt(new Date().clearTime())
					]
			]
	]
	return createBoard(
			'task',
			sourceUUID,
			'responsibleTeam',
			taskForResponsibleEmployeeWithResp,
			states
	)
}
/**
 * Получить объекты системы в представлении для KANBAN. <br>
 * Использовать в качестве кода контента встраиваемого приложения. <br>
 * Задачи на команде по сотрудникам. <br>
 * Принимает на вход:
 * @param sourceUUID хеш текущей карточки объекта
 * @param userUUID уникальный идентификатор пользователя
 * @return json представление для доски KANBAN
 */
def teamTasksByEmployees(sourceUUID, userUUID = null) {
	def states = ['registered', 'inprogress', 'stopped', 'resumed']
	def responsible = [:]
	responsible += [
			notResponsible: [
					title   : 'Без ответственного',
					statuses: [null],
					search  : [
							state              : states,
							responsibleEmployee: null
					]
			]
	]
	responsible += utils.get(sourceUUID).members.collectEntries {
		[
				(it.UUID): [
						title   : it.title,
						statuses: [it.UUID],
						search  : [
								state              : states,
								responsibleEmployee: it.UUID
						]
				]
		]
	}
	return createBoard(
			'task',
			sourceUUID,
			'responsibleTeam',
			taskForResponsibleEmployee,
			responsible,
			'responsibleEmployee'
	)
}