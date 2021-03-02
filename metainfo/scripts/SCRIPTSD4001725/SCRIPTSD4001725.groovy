/*! UTF8 */
//Автор: eboronina
//Дата создания: 02.10.17
//Код: SCRIPTSD4001725
//Назначение: 
/**
 * Подписка на объекты
 * @param FQN_ATTR_CODE - соответствие класса объекта и кода атрибута
 * @param MODE - Подписаться (subscribe) или Отписаться (unsubscribe)
 * Сценарий добавляет или удаляет из подписчиков текущего пользователя
 * ко всем выбранным объектам в списке или к объекту, на карточке
 * которого вызвано действие
 */
//Версия: 4.7.0+
//Категория: Скрипт действия по событию
// ПАРАМЕТРЫ —-------------------------------------------------------
def FQN_ATTR_CODE = [
    'serviceCall' : 'subscribers',
    'task' : 'subscribers'
]
def MODE = 'subscribe'
// ФУНКЦИИ —---------------------------------------------------------
def getNewSubscribers(currentValue, newSub, mode) {
  def result = []
  switch(mode) {
    case 'unsubscribe':
      result = currentValue - newSub
      break
    default:
      result = currentValue + newSub
      break
  }
  return result
}

// ОСНОВНОЙ БЛОК —---------------------------------------------------
if(user != null) {
  // В списке объекты одного класса. Получаем атрибут у произвольного
  def attrCode = FQN_ATTR_CODE[api.metainfo.getMetaClass(subject).fqn.id]
  if(attrCode) {
    subjects.each {
      obj ->
      def value = getNewSubscribers(obj[attrCode], user, MODE)
      utils.edit(obj, [(attrCode) : value])
	}
  }
  else {
    result.showMessage('Ошибка', 'Не определены настройки подписки для объектов данного класса. Обратитесь к администратору системы')
  }
}