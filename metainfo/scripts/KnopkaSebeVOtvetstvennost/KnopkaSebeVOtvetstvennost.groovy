// Если суперпользователь, возвращаем ошибку
if(user == null) {
  result.showMessage('Ошибка', 'Суперпользователь не может быть ответственным за объект')
  return
}

def msg = ''

def res = detectResponsibleOrError(subject)
  
if(res['msg']) {
  result.showMessage('Ошибка', res['msg'])
}
else {
  utils.edit(subject, res)
}


def detectResponsibleOrError(obj) {
  // Ответственная команда
  def team = obj.responsibleTeam
  
  // Ответственная команда не указана
  if(team == null) {
    return ['msg' : 'Нет ответственной команды']
  }
  // Ответственная команда указана
  else {
    // Проверяем, что сотрудник является участником команды
    if(user.teams.find{ team?.UUID == it.UUID && !it.removed }) {
      // Устанавливаем его ответственным в рамках текущей команды
      return ['responsibleEmployee' : user, 'responsibleTeam' : team, '@user' : user]
    }
    return ['msg' : 'Вы не являетесь участником текущей ответственной команды. Воспользуйтесь стандартной формой изменения ответственного']
  }
}