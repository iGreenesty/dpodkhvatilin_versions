 /*! UTF8 */
//Автор: pbutyajkin
//Дата создания: 14.09.17
//Код: SCRIPTSD4001727
//Назначение:
/**
 * Заявка помечается как дубль при переходе в статус Закрыта
 * @param DESCRIPTION_ATTR_CODE - код атрибута Описание
 * @param DUPLICATED_SC_ATTR_CODE - код ссылки Основная заявка
 * @param RESULT_REPORT_ATTR_CODE - код атрибута Отчёт о выполнении
 * @param SHORTDESCR_ATTR_CODE - код атрибута Тема
 * @param REPORTS_OFF_CODE - код атрибута Не учитывать в отчётах
 * @param RESULTDESCR_TEXT - новый отчёт о выполнении
 * @param CODE_OF_CLOSING_ATTR_CODE - код атрибута Код решения
 * @param CATALOG_CODE - код справочника с кодами решения
 * @param CODE_TAKE - код элемента справочника
 * На форме смены статуса выбирается основная заявка. В неё
 * уходит приватный комментарий с информацией о заявке.
 */
//Версия: 4.6.*
//Категория:
import groovy.xml.StreamingMarkupBuilder;
//Параметры------------------------------------------------------
def SPAM_CODE = 'spam'
def DESCRIPTION_ATTR_CODE = 'descriptionRTF'
def DUPLICATED_SC_ATTR_CODE = 'createUp'
def RESULT_REPORT_ATTR_CODE = 'resultDescr'
def SHORTDESCR_ATTR_CODE = 'shortDescr'
def REPORTS_OFF_CODE = 'reportsOff'
def RESULTDESCR_TEXT = 'Дубль'
def CODE_OF_CLOSING_ATTR_CODE = 'codeOfClosing'
def CATALOG_CODE = 'closureCode'
def CODE_TAKE = 'take'
def MSG_ERROR = 'Возникла ошибка при закрытии заявки как дубль. Обратитесь к администратору'
//Функции--------------------------------------------------------
//Основной блок -------------------------------------------------
// Проверяем, что заявка не закрывается как спам
if(initialValues[(CODE_OF_CLOSING_ATTR_CODE)]?.code != (SPAM_CODE)) {
  // сначала проверяем, что дублирующая заявка - не текущая заявка
  if(subject.UUID == subject[(DUPLICATED_SC_ATTR_CODE)]?.UUID) {
    def msg = 'В качестве дубля не может быть текущее обращение'
    utils.throwReadableException(msg, [] as String[], msg, [] as String[])
  }
  
  def html = new StreamingMarkupBuilder().bind{
    table(border:"0", cellpadding:"0", cellspacing:"0", width:"100%") {
      tr(valign:"top") {
        td() {
          strong(style: "font-size:12px","Тема:")
        }
        td() {
          strong(style: "font-weight:normal; color:#505050; padding:0 0 0 10px; font-size:12px", subject[SHORTDESCR_ATTR_CODE])
        }
      }
      tr(valign:"top") {
        td() {
          strong(style: "font-size:12px","Заявитель:")
        }
        td() {
          strong(style: "font-weight:normal; color:#505050; padding:0 0 0 10px; font-size:12px", subject?.client?.title ?: "Не указано")
        }
      }
      tr(valign:"top") {
        td() {
          strong(style: "font-size:12px","Описание дубля:")
        }
        td() {
          strong(style: "font-weight:normal; color:#505050; padding:0 0 0 10px; font-size:12px", api.string.htmlToText(subject[DESCRIPTION_ATTR_CODE]))
        }
      }
    }
  };

    try {
      def editAttrs = [:]
      editAttrs[RESULT_REPORT_ATTR_CODE] = RESULTDESCR_TEXT
      editAttrs[REPORTS_OFF_CODE] = true
      editAttrs['@user'] = user
      if(subject[CODE_OF_CLOSING_ATTR_CODE] == null) {
        def element = utils.get(CATALOG_CODE, ['code' : CODE_TAKE])
        editAttrs[CODE_OF_CLOSING_ATTR_CODE] = element
      }
      utils.edit(subject, editAttrs)
      utils.create('comment', ['source': subject[DUPLICATED_SC_ATTR_CODE].UUID, 'author':user, 'text': html, 'private':true])
    }
  catch (Exception e){
    logger.error("Помечает заявку как Дубль. Возникла ошибка", e)
    utils.throwReadableException(MSG_ERROR, [] as String[], MSG_ERROR, [] as String[])
  }
}