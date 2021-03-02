// Скрипт кастомизации - исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

def client = "";
if(subject.clientOU == null && subject.clientEmployee == null) {
  client = "[не указан]"
}
else {
  client = (subject.clientEmployee) ? "${subject.clientEmployee.title} / " : "";
  client = client + subject.clientOU?.title
}
notification.scriptParams['client'] = client

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''