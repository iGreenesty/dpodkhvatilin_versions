//Получаем ответственного за заявку 
def resp = (subject.responsibleEmployee) ? (subject.responsibleEmployee.title + ' / ') : ''
def respTeam = (subject.responsibleTeam) ? (subject.responsibleTeam.title) : ''
push.scriptParams['resp'] = (resp + respTeam) ?: '[не указан]'

//Получаем клиента в заявке
def client = (subject.clientEmployee) ? (subject.clientEmployee.title + ' / ') : ''
def clientOU = (subject.clientOU) ? (subject.clientOU.title) : ''
push.scriptParams['client'] = (client + clientOU) ?: '[не указан]'

//Если задана услуга и у неё задан ответственный, добавляем его к получателям
if(subject.service != null && subject.service.responsibleEmployee != null) {
  push.toEmployee << subject.service.responsibleEmployee
}

// Исключение ответственного из получателей
def respEmpl = subject.responsibleEmployee
if(respEmpl != null) {
  push.toRemoveEmployee << respEmpl
}