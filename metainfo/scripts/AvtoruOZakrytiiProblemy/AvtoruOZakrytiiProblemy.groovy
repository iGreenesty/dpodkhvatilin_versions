// Подпись к оповещению автора по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

// Исключение автора действия из получателей
if(user != null) {
  notification.toEmployee.remove(user)
  notification.to.remove(user.email)
}