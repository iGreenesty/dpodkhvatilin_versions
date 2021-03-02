// Сообщение
notification.scriptParams['message'] = {
  currentRecipient ->
  def link = modules.notificationAccessHelper.newHyperlink(currentRecipient, subject, subject.title)
  return (subject.responsibleEmployee) ? "Вашей задачи ${link}" : "задачи ${link} в ответственности вашей команды"
}

// Скрипт кастомизации - исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';