def errs = []
if(subject.login == null)
{
  errs << 'логин'
}
if(subject.email == null)
{
  errs << 'адрес электронной почты'
}
if(errs.size() > 0)
{
  def mes = 'У пользователя не установлен ' + errs.join(' и ') + '. Нужно указать значения полей или отменить генерацию пароля для сотрудника.'
  utils.throwReadableException("%s", [mes] as String[], "%s", [mes] as String[]);
}