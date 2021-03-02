/*! UTF8 */
/*& 3600cache */
//Автор: mdemyanov
//Дата создания: 28.12.16
//Код: SCRIPTSD4001407
//Назначение:
/**
 * Установка значения по умолчанию на форме из связанного объекта:
 * @SOURCE - ссылка на объект источник данных
 * @SOURCE_ATTR_CODE - код запрашиваемого атрибута
 */
//Версия: 4.4.x
//Категория: Значение на форме

//Параметры------------------------------------------------------
def SOURCE = 'templateTask'

def ATTRS_FOR_UPDATE_ON_FORMS = [SOURCE]
if (form == null) {
	return ATTRS_FOR_UPDATE_ON_FORMS
}

def SOURCE_ATTR_CODE = 'responsible'
def FORM_ATTR_CODE = 'resp'

//Функции--------------------------------------------------------

//Основной блок -------------------------------------------------
def scriptResult = form[FORM_ATTR_CODE]
def source = form[SOURCE]
if (source != null)
{
	scriptResult = source[SOURCE_ATTR_CODE] ?: scriptResult
}
return scriptResult