//Получаем клиента в заявке
def client = (subject.clientEmployee) ? (subject.clientEmployee.title + ' / ') : ''
def clientOU = (subject.clientOU) ? (subject.clientOU.title) : ''
push.scriptParams['client'] = (client + clientOU) ?: '[не указан]'