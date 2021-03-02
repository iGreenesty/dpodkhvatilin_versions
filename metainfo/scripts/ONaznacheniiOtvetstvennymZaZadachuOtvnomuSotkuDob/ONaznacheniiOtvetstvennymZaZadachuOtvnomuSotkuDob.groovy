def recipient = subject.responsibleEmployee
def deadline = subject.deadline
push.scriptParams['deadline'] = (deadline) ? modules.itsm365.getDateTimeInUserTimeZone(deadline, recipient) : '[не указан]'

def planDate = subject.planDate
push.scriptParams['planDate'] = (planDate) ? modules.itsm365.getDateTimeInUserTimeZone(planDate, recipient) : '[не указана]'

if(user) {
  push.toRemoveEmployee << user
}