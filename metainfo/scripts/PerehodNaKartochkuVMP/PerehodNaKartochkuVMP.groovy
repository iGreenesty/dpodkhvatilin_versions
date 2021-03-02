// Переход на карточку в МК
pushMobile.link << api.web.open(subject)

// Исключение текущего пользователя из числа получателей
if(user) {
  pushMobile.toRemoveEmployee << user
}

pushMobile.scriptParams['root'] = utils.get('root', [:]).title