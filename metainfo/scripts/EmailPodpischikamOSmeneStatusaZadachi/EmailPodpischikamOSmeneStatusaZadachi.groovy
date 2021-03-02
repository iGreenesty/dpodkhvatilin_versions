// Скрипт кастомизации - исключает автора действий из получателей оповещения.
if(null != user) { // текущий пользователь - не супер-пользователь
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

// Подпись к оповещению по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';