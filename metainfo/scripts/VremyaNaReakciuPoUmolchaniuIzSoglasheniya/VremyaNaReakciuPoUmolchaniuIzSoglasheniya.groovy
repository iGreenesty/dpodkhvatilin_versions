def result = null
if(subject?.agreement) {
result = subject.agreement.timeToReact
}
else {

def scUUID = subject.getProperties().UUID

if(scUUID) {
result = utils.get(scUUID).agreement?.timeToReact
}
}
return result ?: api.types.newDateTimeInterval(2, 'HOUR')
