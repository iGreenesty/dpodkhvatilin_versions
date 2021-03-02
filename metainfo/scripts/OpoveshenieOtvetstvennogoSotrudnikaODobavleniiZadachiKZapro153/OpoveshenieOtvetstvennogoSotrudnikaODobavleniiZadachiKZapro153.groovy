// Добавить в качестве получателя оповещения ответственного за заявку сотрудника
notification.toEmployee << subject.serviceCall.responsibleEmployee

// Скрипт кастомизации - исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

notification.scriptParams['description'] = subject.serviceCall?.descriptionRTF?: "[не указано]"