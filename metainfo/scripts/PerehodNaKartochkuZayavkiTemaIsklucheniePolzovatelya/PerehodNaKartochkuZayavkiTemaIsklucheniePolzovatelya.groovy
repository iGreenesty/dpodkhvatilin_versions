def maxChars = 30
def shortDescr = subject.shortDescr
def end = Math.min(shortDescr.size(), maxChars)
pushMobile.scriptParams['shortDescr'] = shortDescr.substring(0, end)

pushMobile.link << api.web.open(subject)

if(user) {
  pushMobile.toRemoveEmployee << user
}

pushMobile.scriptParams['root'] = utils.get('root', [:]).title