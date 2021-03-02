def result = null
if(subject?.agreement) {
result = subject.agreement.timeToSolve
}
else {

def scUUID = subject.getProperties().UUID

if(scUUID) {
result = utils.get(scUUID).agreement?.timeToSolve
}
}
return result ?: api.types.newDateTimeInterval(8, 'HOUR')
