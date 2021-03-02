//Автор: vkuznetsov
//Дата создания: 29.01.2019
//Назначение:
/**
 * Заполняет значение атрибута Для автозакрытия заявки 
 *  @param STATUS_CHANGE_TIME - код атрибута, в который записывается время потпытки закрытия
 *  @param AUTO_CLOSE_DAYS - код атрибута, который определяет через сколько дней решённая заявка закрывается
 * Сценарий устанавливает время для закрытия заявки через количество рабочих дней, указанных в карточке компании.
 * Если атрибут с днями не заполнен, то ничего не происходит.
 */
//Версия: 4.11.0+
//Категория: Статусы запроса, действие на вход/выход из статуса

//ПАРАМЕТРЫ------------------------------------------------------------
def STATUS_CHANGE_TIME = 'timeClosing24'
def AUTO_CLOSE_DAYS = 'NScClDays' 

def daysToClose = utils.get('root', [:])[AUTO_CLOSE_DAYS];
if (daysToClose != null){
  	def date = api.timing.addWorkingDays(new Date(), daysToClose.toInteger(), subject.serviceTime, subject.timeZone)
	utils.edit(subject, [ (STATUS_CHANGE_TIME) : date ])	
}
