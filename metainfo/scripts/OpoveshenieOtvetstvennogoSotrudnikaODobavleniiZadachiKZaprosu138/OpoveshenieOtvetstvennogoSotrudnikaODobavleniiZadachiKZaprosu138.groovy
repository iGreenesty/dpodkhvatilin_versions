// Добавить в качестве получателя оповещения ответственного за запрос на изменение сотрудника
notification.toEmployee << subject.changeRequest.responsibleEmployee

// Скрипт кастомизации - исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}


// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';