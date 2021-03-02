// Добавление в получатели оповещения ответственного за раздел БЗ
notification.toEmployee << subject.parent.responsibleEmployee
notification.scriptParams['author'] = (subject.author) ? subject.author.title : 'Суперпользователь'

// Исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''                    