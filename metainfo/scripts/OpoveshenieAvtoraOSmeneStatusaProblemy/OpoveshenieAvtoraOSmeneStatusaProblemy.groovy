// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';
// Исключает автора действий из получателей оповещения
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

