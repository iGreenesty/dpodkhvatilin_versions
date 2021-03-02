/*! UTF8 */
//Автор: mdemyanov
//Дата создания: 14.09.17
//Код: SCRIPTSD4001695
//Назначение: 
/**
* [Заявка] Пометить как спам
* @param ACTIONS правила связи атрибутов заявки и справочников черных списков
* @param PRE_ACTIONS функции для пердварительной обработки
* @param CODE_SPAM код закрытия СПАМ
* @param SCALL_FQNS типы заявок, для которых доступно действие
*/
//Версия: 4.6.*
//Категория: 
//Параметры------------------------------------------------------
def ACTIONS = [
  'blackListEmails' : 'clientEmail',
  'blackListSubject':'shortDescr',
  'blackListContent':'descriptionRTF'
]
def PRE_ACTIONS = [
  descriptionRTF: 'htmlToString256'
]
def CODE_SPAM = 'spam'
def SCALL_FQNS = ['serviceCall$call']
//Функции--------------------------------------------------------
/**
* Очистить от пробельных символов
* @param str строка с пробельными символами
*/
def replaceAllWhitespaces(str) {
  return str.replaceAll('(&nbsp;|\u00A0|\u2000|\u2001|\u2002|\u2003|\u2004|\u2005|\u2006|\u2007|\u2008|\u2009|\u200A|\u200B|\u202F|\u205F|\u3000|\uFEFF)', ' ')
}

/**
* Получить чистый текст до 256 символов:
* @param value значение атрибута текста в формате RTF
*/
def htmlToString256(value) {
  value = replaceAllWhitespaces(api.string.htmlToText(value))
  return value.substring(0, Math.min(value.length(), 256))
}
/**
* Создать новый элемент в каталогах черных списков
* @param fqn код класса справочника
* @param title название элемента справочника
* @param subjectId идентификатор объекта источника
*/
def createCatalogElement(fqn, title, subjectId) {
  def currentElements = utils.find(fqn, [title:title])
  if (currentElements.size() == 0) {
    def attrs = [:]
    attrs.title = title
    attrs.code = subjectId
    try {
      api.tx.call{
        utils.create(fqn, attrs)
      }
    }
    catch (Exception e) {
      logger.error("[Кнопка] Пометить как спам. Во время добавления элемента ${fqn} для ${subjectId} возникла ошибка", e)
      def msg = "Возникла ошибка при добавлении элемента в черный список. Обратитесь к администратору."
      utils.throwReadableException(msg, [] as String[], msg, [] as String[])
    }
  } else {
    logger.info("[Кнопка] Пометить как спам. Элемент с '${title}' уже существует")
  }
}
/**
* Проверить текущие справочники на полноту
* @param serviceCall объект заявка
* @param preActions дополнительные правила прелоразования
* */
def checkAction(serviceCall, preActions) {
  {catalog, attr->
    def value = serviceCall[attr]
    if (value) {
      value = (preActions[attr] ? "${preActions[attr]}"(value) : value).trim()
      if(value != '') {
        createCatalogElement(catalog, value, subject.UUID)
      }
    }
  }
}
//Основной блок -------------------------------------------------
def codeOfClosing = utils.get('closureCode', ['code' : CODE_SPAM])
def scallAttrs = [
  codeOfClosing: codeOfClosing,
  resultDescr: 'Спам',
  state: 'closed',
  reportsOff : true,
  '@user' : user
]
def actions = ACTIONS.findAll{catalog, attr ->
  return params[catalog]
}
subjects.each{serviceCall->
  if (serviceCall.metaClass.toString() in SCALL_FQNS) {
    actions.each(checkAction(serviceCall, PRE_ACTIONS))
    try {
      utils.edit(serviceCall, scallAttrs, true)
    }
    catch (Exception e) {
      logger.error("[Кнопка] Пометить как спам. При переходе в статус закрыто произошла ошибка", e)
      def msg = "Возникла ошибка при закрытии заявки. Обратитесь к администратору."
      utils.throwReadableException(msg, [] as String[], msg, [] as String[])
    }
  }
}
