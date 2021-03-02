def deadline = subject.deadline
if(deadline)
{	
  def recipient = subject.responsibleEmployee
  def formated = modules.itsm365.getDateTimeInUserTimeZone(deadline, recipient)
  push.scriptParams['deadline']  = formated
}
else 
{
  push.scriptParams['deadline'] = '[не указан]'
}

if(user)
{
  push.toRemoveEmployee << user
}