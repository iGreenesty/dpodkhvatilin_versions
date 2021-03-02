/*! UTF8 */
//Автор: eboronina
//Дата создания: 27.07.2020
//Код: SCRIPTSD4002259
//Назначение:
/**
 * Обновление времени эскалации и индикатора просроченности
 * @param INFORM_BY - коэффициент, за сколько процентов
 * оповещать по эскалации. 0.3 = 30%
 * @param SERVICE_TIME - код атрибута Класс обслуживания
 * @param TIME_ZONE - код атрибута Часовой пояс
 * @param PLANDATE - код атрибута Плановая дата начала
 * @param DECISION_DATE - код атрибута Дата решения
 * @param DEADLINE - код атрибута Дедлайн
 * @param OVERDUE_ATTR - код атрибута Просрочен
 * @param NOT_OVERDUE - код элемента Не просрочен
 * @param OVERDUE - код элемента Просрочен
 * @param DONE_STATES - коды статусов, в которых задача
 * считается выполненной
 * Сценарий срабатывает при изменении плановой даты начала, дедлайна
 * или статуса. Пересчитывает время эскалации по задаче, если оно
 * требуется. И пересчитывает индикатор просроченности задачи, в
 * зависимости от дедлайна и даты решения или текущей даты, если
 * задача ещё не выполнена.
 */
//Версия: 4.11.0+
//Категория: 
//Параметры-----------------------------------------------------
def INFORM_BY = 0.3
def SERVICE_TIME = 'serviceTime'
def TIME_ZONE = 'timeZone'
def PLANDATE = 'planDate'
def ESCALATEDATE = 'email30Date'
def DECISION_DATE = 'dateDecision'
def DEADLINE = 'deadline'
def OVERDUE_ATTR = 'isOverdue'
def NOT_OVERDUE = 'notOverdue'
def OVERDUE = 'overDue'
def DONE_STATES = ['resolved', 'closed']
//Функции-------------------------------------------------------

//Основной блок ------------------------------------------------
def escalationDate
def overdue
def deadline = subject[DEADLINE]
// Если дедлайн не указан
if (deadline == null) {
  // Эскалации нет
  escalationDate = null
  // Задача не просрочена
  overdue = NOT_OVERDUE
}
// Если дедлайн указан
else {
  // Если задача выполнена или закрыта
  if(subject.state in DONE_STATES) {
    // Эскалации нет
    escalationDate = null
    // Просроченность проверяем от дедлайна и даты решения
    overdue = (subject[DECISION_DATE] <= deadline) ? NOT_OVERDUE : OVERDUE
  } else {
    // Если задача не выполнена и не закрыта
    // Класс обслуживания в задаче
    def serviceTime = subject[SERVICE_TIME]
    // Часовой пояс в задаче
    def timeZone = subject[TIME_ZONE]
    // Дата начала работы над задачей - плановая или дата создания
    def startDate = subject[PLANDATE] ?: subject.creationDate
    // Сколько секунд отводится от плана
    def timeToSolveIntervalSeconds = api.timing.serviceTime(serviceTime, timeZone, startDate, deadline) / 1000
    // Определяем за сколько секунд от дедлайна будет эскалация
    def percentageInSeconds = (timeToSolveIntervalSeconds * INFORM_BY).toInteger()
    // Определяем возможную дату эскалации как дедлайн минус количество секунд
    def mayBeEscalateDay = use(groovy.time.TimeCategory) { subject[DEADLINE] - percentageInSeconds.seconds }
    // Если дата эскалации в будущем, то устаналиваем её
   	def now = new Date()
    escalationDate = (now <= mayBeEscalateDay) ? mayBeEscalateDay : null
    // Просроченность проверяем от дедлайна и текущего времени
    overdue = (now <= deadline) ? NOT_OVERDUE : OVERDUE
  }
}
utils.edit(subject, [(OVERDUE_ATTR) : overdue, (ESCALATEDATE) : escalationDate])