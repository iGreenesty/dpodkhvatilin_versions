// Добавить в качестве получателя оповещения сотрудника, на которого оформлена трудозатрата
notification.toEmployee << subject.employee

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

// Исключение автора действия из получателей
if(user != null) {
  notification.toEmployee.remove(user)
  notification.to.remove(user.email)
}