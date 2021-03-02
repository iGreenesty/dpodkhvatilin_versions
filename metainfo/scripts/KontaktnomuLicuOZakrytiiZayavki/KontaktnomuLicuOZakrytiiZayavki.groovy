// Если заполнено имя контактного лица, то обращаемся по имени, иначе - "клиент"
notification.scriptParams['greeting'] = subject.clientName ? "Уважаемый(ая) ${subject.clientName}" : "Уважаемый клиент"

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

// Уточнение решения заявки
notification.scriptParams['resCode'] = 'была закрыта.'
if(subject.codeOfClosing?.code == 'take') {
  // Как дубль
  notification.scriptParams['resCode'] = 'была закрыта как дубль.'
}
if(subject.codeOfClosing?.code == 'spam') {
  // Как спам
  notification.scriptParams['resCode'] = 'была закрыта как спам.'
}

//Добавляет контактный e-mail в список получателей оповещения.
def CLIENT_EMAIL = 'clientEmail' // Код атрибута "Контактный е-мэйл"
def CLIENT_NAME = 'clientName' // Код атрибута "Контактное лицо"
def clientEmail = subject[CLIENT_EMAIL]
notification.to[clientEmail] = subject[CLIENT_NAME]

//Если новый статус - resolved, удаляем из списка оповещаемых контрагента
if(user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}