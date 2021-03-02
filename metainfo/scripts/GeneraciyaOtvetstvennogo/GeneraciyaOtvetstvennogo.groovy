//Получаем ответственного за заявку 
def resp = (subject.responsibleEmployee) ? (subject.responsibleEmployee.title + ' / ') : ''
def respTeam = (subject.responsibleTeam) ? (subject.responsibleTeam.title) : ''
push.scriptParams['resp'] = (resp + respTeam) ? (resp + respTeam) : '[не указан]'

//Получаем клиента в заявке
def client = (subject.clientEmployee) ? (subject.clientEmployee.title + ' / ') : ''
def clientOU = (subject.clientOU) ? (subject.clientOU.title) : ''
push.scriptParams['client'] = (client + clientOU) ?: '[не указан]'

// Исключение ответственного из получателей
def respEmpl = subject.responsibleEmployee
if(respEmpl != null) {
  push.toRemoveEmployee << respEmpl
}