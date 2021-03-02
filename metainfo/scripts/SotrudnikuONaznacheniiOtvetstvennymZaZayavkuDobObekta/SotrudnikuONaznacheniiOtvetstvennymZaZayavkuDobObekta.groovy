// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

//Описание заявки, если пустое то выводим тему
notification.scriptParams['description'] = subject.descriptionRTF ?: '[не указано]';

def client = "";
if(subject.clientOU == null && subject.clientEmployee == null) {
  client = "[не указан]"
}
else {
  client = (subject.clientEmployee) ? "${subject.clientEmployee.title} / " : "";
  client = client + subject.clientOU?.title
}
notification.scriptParams['client'] = client

// Исключение автора действия из получателей
if(user != null) {
  notification.toEmployee.remove(user)
  notification.to.remove(user.email)
}