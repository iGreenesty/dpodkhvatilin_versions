// Автор: eboronina
// Дата создания: 26.07.2018
/**
 * Скрипт на вход в состояние (с кодом registered)
 * @params REQUEST_TYPE - Код атрибута "Способ обращения" в Запросе
 * @params CATALOG_ITEM_LK - Код элемента для способа обращения "ЛК"
 * @params CATALOG_ITEM_PHONE - Код элемента для способа обращения "Телефон"
 * @params CATALOG_ITEM_MOBILE_APP - Код элемента для способа обращения "Из мобильного приложения"
 * Если атрибут уже заполнен (при регистрации по почте), то ничего не происходит.
 * Иначе проверяем на совпадение автора заявки и контрагента. Если совпадают,
 * то из личного кабинета, иначе - по телефону.
 */
// Версия: 4.0
// Категория: Статусы запроса, действие на вход/выход из статуса

//ПАРАМЕТРЫ------------------------------------------------------------
// Код атрибута "Способ обращения" типа "элемент справочника" в классе "Запрос"
def REQUEST_TYPE = 'wayAddressing'
// Код элемента справочника для способа обращения "ЛК"
def CATALOG_ITEM_LK = 'SD'
// Код элемента справочника для способа обращения "телефон"
def CATALOG_ITEM_PHONE = 'phone'
// Код элемента справочника для способа обращения "Из мобильного приложения"
def CATALOG_ITEM_MOBILE_APP = 'fromMobileApp'

//ОСНОВНОЙ БЛОК--------------------------------------------------------
def subjectMetaClass = api.metainfo.getMetaClass(subject.getMetainfo())
if (!subjectMetaClass.hasAttribute(REQUEST_TYPE)) {
  logger.error("Скрипт автозаполнения способа обращения. Не найден атрибут '${REQUEST_TYPE}'.")
  return ''
}

def requestTypeValue = subject[REQUEST_TYPE]
if (requestTypeValue == null) {
  def map = [:]
  if(api.actionContext.isMobile()) {
    requestTypeValue = CATALOG_ITEM_MOBILE_APP
  }
  else if(subject.author && subject.clientEmployee && (subject.author.UUID).equals(subject.clientEmployee.UUID)) {
    requestTypeValue = CATALOG_ITEM_LK
  }
  else {
    requestTypeValue = CATALOG_ITEM_PHONE
  }
  map.put(REQUEST_TYPE, requestTypeValue)
  try {
    utils.edit(subject, map)
  } catch (e) {
    logger.error("Скрипт автозаполнения способа обращения. Ошибка", e)
    def msg = 'Возникла ошибка при вычисления способа обращения. Обратитесь к администратору'
    utils.throwReadableException(msg, [] as String[], msg, [] as String[])
  }
}