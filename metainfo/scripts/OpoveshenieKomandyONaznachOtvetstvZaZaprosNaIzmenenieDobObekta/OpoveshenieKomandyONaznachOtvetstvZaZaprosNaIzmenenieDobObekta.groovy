// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''
notification.scriptParams['author'] = (subject.author) ? subject.author.title : 'Суперпользователь'

// Скрипт кастомизации - исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

//Описание ЗНИ
notification.scriptParams['description'] = subject.description