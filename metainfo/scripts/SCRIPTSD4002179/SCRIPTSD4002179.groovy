/*! UTF8 */
//Автор: eboronina
//Дата создания: 20.12.2019
//Код: SCRIPTSD4002179
//Назначение:
/**
 * Фильтрация разделов базы знаний по типам
 * @param TYPES - определение метаклассов разделов в
 * зависимости от значения флажка "Внутренняя"
 * @param FLAG_CODE - код параметра флажка "Внутренняя"
 * @param ATTRS_FOR_UPDATE - атрибуты, от которых зависит
 * Сценарий возвращает объекты типов, в зависимости от флажка
 */
//Версия: 4.10.0+
//Категория: 
//Параметры------------------------------------------------------
// 0 - внешние, 1 - внутрение
def TYPES = [['KB$KBSectionOpen', 'KB$KBSubsectionOp'], ['KB$KBSectionProt', 'KB$KBSubsectionPr']]
def FLAG_ATTR = 'isInternal'
def ATTRS_FOR_UPDATE = [FLAG_ATTR]
//Функции--------------------------------------------------------

//Основной блок -------------------------------------------------
if (form == null) {
  return ATTRS_FOR_UPDATE
}
def fitTypes =  TYPES[form[FLAG_ATTR] ? 1 : 0]
return fitTypes.collect {
  utils.find(it, [:])
}