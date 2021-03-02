/*! UTF8 */
//Автор: eboronina
//Дата создания: 22.01.18
//Код: SCRIPTSD4001838
//Назначение:
/**
 * Фильтрация списка атрибутов в зависимости от доступных значений
 * в выбранном целевом объекте
 * @param TARGET - код атрибута ссылки на целевой объект
 * @param ATTR_CODE - код атрибута доступных значений
 * @param IF_EMPTY_ALL - логический, если не нашлось ни одного объекта, вернуть все
 * @param ATTRS_FOR_UPDATE_ON_FORMS - атрибуты для обновления
 * Сценарий определяет целевой объект и возвращает список
 * доступных значений. Иначе пустой список
 */
//Версия: 4.6.0+
//Категория: Фильтрация значений атрибутов
//Параметры------------------------------------------------------
def TARGET = 'service'
def ATTR_CODE = 'category'
def IF_EMPTY_ALL = false
def ATTRS_FOR_UPDATE_ON_FORMS = [TARGET]
//Функции--------------------------------------------------------
//Основной блок -------------------------------------------------
if (subject == null) {
  return ATTRS_FOR_UPDATE_ON_FORMS
}
def result = (IF_EMPTY_ALL) ? api.filtration.disableFiltration() : []
if (subject[TARGET] && subject[TARGET][ATTR_CODE]) {
  result = subject[TARGET][ATTR_CODE]
}
return result