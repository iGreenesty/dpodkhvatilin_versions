def client = "";
if(subject.clientOU == null && subject.clientEmployee == null) {
  client = "[не указан]"
}
else {
  client = (subject.clientEmployee) ? "${subject.clientEmployee.title} / " : "";
  client = client + subject.clientOU?.title
}
notification.scriptParams['client'] = client

// Получить оценку
if (subject.mark) {
	notification.scriptParams['mark'] = subject.mark.title;
}
else {
	notification.scriptParams['mark'] = '[не указана]'
}

// Получить комментарий к оценке
if (subject.commentForMark) {
	// Получение текста комментария
	notification.scriptParams['commentForMark'] = subject.commentForMark;
}
else {
	notification.scriptParams['commentForMark'] = '[не указан]'
}

// Скрипт кастомизации - исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';