/*! UTF8 */
//Автор: vkuznetsov
//Дата создания: 16.01.20
//Код: SCRIPTSD4002187
//Назначение: stateFromTime
/**
* Перевод заявки в статус выполнена, при достижении времени в атрибуте
* @param STATUS_CHANGE_TIME - код атрибута, в который записывается следующая попытка закрытия
* @param TARGET_STATE - код статуса заявки, для первода
* @param AUTO_CLOSE_DAYS - код атрибута класса 'Компания', в котором содержится колличество дней для следующей попытки закрытия
* Сценарий устанавливает новый статус для заявки
* или же при неудачной смене статуса записывает время следующей попытки смены статуса
*/
//Версия: 4.11.0+
//Категория: Действие по событию типа скрипт
//Параметры------------------------------------------------------

def STATUS_CHANGE_TIME = 'timeClosing24'
def TARGET_STATE = 'closed'
def AUTO_CLOSE_DAYS = 'NScClDays' 

//Функции--------------------------------------------------------
//Основной блок -------------------------------------------------

try
{
  	utils.edit(subject, ['state' : TARGET_STATE, (STATUS_CHANGE_TIME) : null]);
} 
catch(e)
{
    def daysToClose = utils.get('root', [:])[AUTO_CLOSE_DAYS];
    if (daysToClose)
  	{
        def date = api.timing.addWorkingDays(new Date(), daysToClose.toInteger(), subject.serviceTime, subject.timeZone);
        utils.edit(subject, [ (STATUS_CHANGE_TIME) : date ], true);	//Отдельная транзакция для того, чтобы после throw изменения не откатились
    }
    throw e
}