/*! UTF8 */
/*& 3600cache */
//Автор: mdemyanov
//Дата создания: 27.01.17
//Автор изменения: vkuznetsov
//Дата изменения: 21.02.2018
//Код: SCRIPTSD4001896
//Назначение: 
/**
 * На основе скрипта: SCRIPTSD4001434
 * Вычисление дедлайна задачи по шаблону:
 * @param TEMPLATE_TASK - код ссылки на шаблон на форме
 * @param START_TIME - код параметра даты начала на форме
 * @param SERVICE_TIME - код атрибута класса обслуживания в компании
 * @param TIME_ZONE - код атрибута часого пояса в компании
 * @param INTERVAL - код атрибута с временным интервалом
 * Сценарий вычисляет дедлайн в случае заполнения
 * ссылки на шаблон задачи, при наличии заполненного
 * параметра продалжительности в шаблоне. Перерасчет
 * производится при изменении даты начала или шаблона.
 */
//Версия: 4.7.6+
//Категория:

//Параметры-----------------------------------------------------
def TEMPLATE_TASK = 'templateTask'
def START_TIME = 'planDate'

def ATTRS_FOR_UPDATE_ON_FORMS = [TEMPLATE_TASK, START_TIME]
if (form == null) {
	return ATTRS_FOR_UPDATE_ON_FORMS
}

def SERVICE_TIME = 'serviceTime'
def TIME_ZONE = 'timeZone'
def INTERVAL = 'timeToSolve'

//Функции--------------------------------------------------------

//Основной блок -------------------------------------------------
def scriptResult = form[attrCode]
//Определение вспомогательных переменных
def templateTask = form[TEMPLATE_TASK]
def root = utils.get('root', [:])
if (subject == null && templateTask != null)
{
	//Получение параметров для вычисления целевой даты
	def interval = templateTask[INTERVAL]
	/*Вычисляем целевой дедлайн только если он указан в шаблоне*/
	if (interval != null)
    {
		interval = interval.toMiliseconds()
		def serviceTime = root[SERVICE_TIME]
		def timeZone = root[TIME_ZONE]
		def startTime = form[START_TIME] ?: new Date()
		scriptResult = api.timing.serviceEndTime(
				serviceTime,
				timeZone,
				startTime,
				interval)
	}
}
return scriptResult