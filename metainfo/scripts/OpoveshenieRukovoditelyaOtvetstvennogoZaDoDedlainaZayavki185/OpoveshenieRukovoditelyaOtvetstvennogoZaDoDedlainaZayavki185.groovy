// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

//Описание заявки
notification.scriptParams['description'] = subject.descriptionRTF ?: '[не указано]';

def resp = (subject.responsibleEmployee) ? (subject.responsibleEmployee.title + ' / ') : ''
def respTeam = (subject.responsibleTeam) ? (subject.responsibleTeam.title) : ''
notification.scriptParams['resp'] = (resp + respTeam) ? (resp + respTeam) : '[не указан]'

// Исключение ответственного из получателей
def respEmpl = subject.responsibleEmployee
if(respEmpl != null) {
  notification.toEmployee.remove(respEmpl)
  notification.to.remove(respEmpl.email)
}