// Если пользователь типа Контактное лицо и он ключевой
if(user.metaClass.toString() == 'employee$contactPerson' && user.keyEmployee) {
  // Список из родителя сотрудника и всех вложенных в него отделов
  def keyOUs = api.ou.nestedOUs(user.parent)
  // Вернем все заявки, где клиент-отдел из списка
  return api.filters.attrValueIn('clientOU', keyOUs, false)
}
// Иначе ничего не вернем
return api.filters.none()