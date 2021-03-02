//SCRIPTSD4000151 
// Скрипт на вход в статус разрешен, который заполняет поле Кем решен сотрудником, выполнившим переход в статус
//Автор: dzevako
//Дата создания: 12.11.2012
//Назначение:
/**
* Скрипт на вход в статус запроса,
* который заполняет атрибут запроса "Кем решен" текущим пользователем
*/
//Версия: 4.0
//Категория: Статусы запроса, действие на вход/выход из статуса

//ПАРАМЕТРЫ------------------------------------------------------------

AGR_ATTR = 'solvedBy' // код атрибута "Кем решен" (агрегирующий)

//ОСНОВНОЙ БЛОК--------------------------------------------------------

if (null == user)
{
return;
}
else
{
def properties = [:];
def responsibleTeam = subject.responsibleTeam;
def solvedByTeam;

if (null == responsibleTeam)
{
solvedByTeam = null;
}
else
{
solvedByTeam = (user.teams.contains(responsibleTeam)) ? responsibleTeam : null;
} 

def team = api.string.concat("", [AGR_ATTR, "Team"]);
def employee = api.string.concat("", [AGR_ATTR, "Employee"]);

def metaClass = api.metainfo.getMetaClass(subject.getMetainfo())
if (!metaClass.hasAttribute(team) || !metaClass.hasAttribute(employee))
{ 
team = api.string.concat("", [AGR_ATTR, "_te"]);
employee = api.string.concat("", [AGR_ATTR, "_em"]);
}

properties.put(team, solvedByTeam);
properties.put(employee, user);

utils.edit(subject, properties); 
}