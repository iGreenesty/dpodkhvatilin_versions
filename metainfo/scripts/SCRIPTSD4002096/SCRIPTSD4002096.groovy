/*! UTF8 */
//Автор: eboronina
//Дата создания: 12.03.2019
//Код: SCRIPTSD4002096
//Назначение:
/**
 * Закрываем все связанные выполненные задачи
 * @param TASK_ATTR - код атрибута набора ссылок на задачи
 * @param STATE_TO_FIND - код статуса, задачи в котором ищем
 * @param TARGET_STATE - код целевого статуса для задач
 * @param ADDIT_MAP - мапа дополнительных параметров
 * Сценарий получает список всех связанных задач. И пытается
 * каждую перевести в целевой статус. Если не может, пишет ошибку
 * в лог.
 */
//Версия: 4.9.0+
//Категория: 
//Параметры------------------------------------------------------
def TASK_ATTR = 'tasks'
def STATE_TO_FIND = 'resolved'
def TARGET_STATE = 'closed'
def ADDIT_MAP = [
  '@user' : user
]
//Функции--------------------------------------------------------
//Основной блок -------------------------------------------------
def resolvedTasks = subject[(TASK_ATTR)].findAll { it.state == STATE_TO_FIND}
def map = ADDIT_MAP
map.put('state', TARGET_STATE)
resolvedTasks.each {
  task ->
  try {
    utils.edit(task, map)
  } catch (Exception e) {
    logger.error("Закрываем все связанные выполненные задачи. Ошибка при закрытии задачи ${task.title}", e)
    def msg = "Возникла ошибка при закрытии задач. Обратитесь к администратору."
    utils.throwReadableException(msg, [] as String[], msg, [] as String[])
  }
}