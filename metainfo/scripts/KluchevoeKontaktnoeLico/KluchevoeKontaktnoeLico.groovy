// Если есть объект, пользователь типа Контактное лицо и он ключевой
if(subject && user.metaClass.toString() == 'employee$contactPerson' && user.keyEmployee) {
  // Список из родителя сотрудника и всех вложенных в него отделов
  def keyOUs = api.ou.nestedOUs(user.parent)
  // Вернем да, если клиент-отдел заявки есть в списке
  return keyOUs.contains(subject.clientOU)
}
// В остальных случаях - нет
return false