// Добавить в качестве получателя оповещения ответственного за запрос на изменение сотрудника
notification.toEmployee << subject.sourceR.responsibleEmployee

// Исключает автора действий из получателей оповещения.
if(null != user) {
  notification.to.remove(user.email)
  notification.toEmployee.remove(user)
}

// Подпись к оповещению
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''