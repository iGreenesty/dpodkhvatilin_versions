def DEFAULT_EMPLOYEE_NAME = "Служебный"
if ((subject.clientEmployee == null || subject.clientEmployee?.title == DEFAULT_EMPLOYEE_NAME) && !utils.get('root', [:]).notifUnregist) {
  return 'контрагент Служебный, при этом оповещение служебного сотрудника отключено в настройках'
}
return ''