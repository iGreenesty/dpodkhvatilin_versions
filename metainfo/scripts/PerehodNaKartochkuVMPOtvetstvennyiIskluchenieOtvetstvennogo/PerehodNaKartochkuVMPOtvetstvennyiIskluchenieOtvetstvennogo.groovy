pushMobile.link << api.web.open(subject)

pushMobile.scriptParams['root'] = utils.get('root', [:]).title

def resp = (subject.responsibleEmployee) ? (subject.responsibleEmployee.title + ' / ') : ''
def respTeam = (subject.responsibleTeam) ? (subject.responsibleTeam.title) : ''
pushMobile.scriptParams['resp'] = (resp + respTeam) ?: '[не указан]'

// Исключение ответственного из получателей
def respEmpl = subject.responsibleEmployee
if(respEmpl != null) {
  pushMobile.toRemoveEmployee << respEmpl
}