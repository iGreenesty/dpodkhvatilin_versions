def ATTRS_FOR_UPDATE_ON_FORMS = ['client', 'clientOU', 'clientEmployee']

if(null == subject) {
  return ATTRS_FOR_UPDATE_ON_FORMS
}

def KEs = []
// Текущие ИТ-активы оставляем
KEs.addAll(subject.KEs)

if (subject.clientEmployee != null) {
  // Если есть контрагент-сотрудник, то
  // всё его оборудование в пользовании
  KEs.addAll(subject.clientEmployee.KEsInUse)
}
else if(subject.clientOU != null) {
  // Если сотрудника нет, но есть отдел, то
  def clientOU = subject.clientOU
  // всё его оборудование во владении
  KEs.addAll(clientOU.KEsInUse)
  // + всё оборудование сотрудников в пользовании
  KEs.addAll(clientOU.employees.KEsInUse.flatten())
}
return KEs