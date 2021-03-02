def ATTRS_FOR_UPDATE_ON_FORMS = []
// Обязательная проверка! Даже если фильтрующих атрибутов нет.
if (subject == null) {
	return ATTRS_FOR_UPDATE_ON_FORMS
}
def result = utils.find('employee', ['license' : op.not('notLicensed')])
return result