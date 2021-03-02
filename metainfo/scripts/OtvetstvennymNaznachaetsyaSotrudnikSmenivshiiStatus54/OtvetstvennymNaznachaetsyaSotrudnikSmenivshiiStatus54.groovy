//Автор: ashelgacheva
//SCRIPTSD4000891
//Дата создания: 22.04.2015
//Назначение:
/**
 * назначение скрипта : Ответственным назначается сотрудник сменивший статус иначе остается текущий ответственный.
 */

//Категория:

//ОСНОВНОЙ БЛОК------------------------------------------------
if ((subject.masterMassProblem != null) || (user == null) || !user.teams)
{
    // если заявка является подчинённой, или текущий пользователь - суперпользователь,
    // или пользователь не состоит ни в одной из команд, завершаем работу

    return
}

// установить ответственного сотрудника текущим пользователем
def responsibleEmployee = user
def responsibleTeam

if ((user.teams.size() > 1) && (subject.responsibleTeam != null) && user.teams.contains(subject.responsibleTeam))
{
    // если:
    // * сотрудник состоит в нескольких командах, и
    // * за заявку назначена ответственная команда, и
    // * данный сотрудник состоит в данной команде, то
    // оставить ответственной за заявку текущую команду
    responsibleTeam = subject.responsibleTeam
}
else
{
    // установить ответственной командой любую команду текущего пользователя (единственную, если она одна) 
    responsibleTeam = user.teams[0]
}

utils.edit(subject, ['responsibleEmployee': responsibleEmployee, 'responsibleTeam': responsibleTeam, '@user' : user])