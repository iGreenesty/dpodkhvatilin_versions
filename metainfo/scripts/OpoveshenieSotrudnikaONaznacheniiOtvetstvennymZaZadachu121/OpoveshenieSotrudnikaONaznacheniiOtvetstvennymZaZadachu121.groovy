notification.scriptParams['greeting'] = (subject.responsibleEmployee) ? 'Вы назначены ответственным' : 'Ваша команда назначена ответственной'
// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''

// Исключение автора действия из получателей
if(user != null) {
  notification.toEmployee.remove(user)
  notification.to.remove(user.email)
}