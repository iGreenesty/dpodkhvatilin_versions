pushMobile.link << api.web.open(subject)
pushMobile.scriptParams['root'] = utils.get('root', [:]).title

// Исключение ответственного из получателей
def respEmpl = subject.responsibleEmployee
if(respEmpl != null) {
  pushMobile.toRemoveEmployee << respEmpl
}