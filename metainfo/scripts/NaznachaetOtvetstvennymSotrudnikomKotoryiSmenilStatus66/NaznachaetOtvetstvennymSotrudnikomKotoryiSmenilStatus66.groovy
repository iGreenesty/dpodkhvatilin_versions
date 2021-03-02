//Автор: ashelgacheva
//SCRIPTSD4000891
//Дата создания: 22.04.2015
//Назначение:
/**
* назначение скрипта : Ответственным назначается сотрудник сменивший статус иначе остается текущий ответственный.
*/

//Категория:

def STATES = ['closed', 'stopped']
//ОСНОВНОЙ БЛОК------------------------------------------------
if (user == null || !user.teams || STATES.contains(oldSubject.state)) {
  // если или текущий пользователь - суперпользователь,
  // или пользователь не состоит ни в одной из команд,
  // или задача перешла в работу из списка статусов завершаем работу

  return ;
}

// установить ответственного сотрудника текущим пользователем
def responsibleEmployee = user
def responsibleTeam
if ((user.teams.size() > 1) && (subject.responsibleTeam != null) && user.teams.contains(subject.responsibleTeam)) {
  // если:
  // * сотрудник состоит в нескольких командах, и
  // * за задачу назначена ответственная команда, и
  // * данный сотрудник состоит в данной команде, то
  // оставить ответственной за задачу текущую команду
  responsibleTeam = subject.responsibleTeam
}
else {
  // установить ответственной командой любую команду текущего пользователя (единственную, если она одна) 
  responsibleTeam = user.teams[0]
}

utils.edit(subject, ['responsibleEmployee': responsibleEmployee, 'responsibleTeam': responsibleTeam, '@user' : user])