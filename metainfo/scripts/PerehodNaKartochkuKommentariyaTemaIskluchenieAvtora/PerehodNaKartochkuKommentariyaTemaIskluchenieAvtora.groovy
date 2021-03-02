def maxChars = 30
def shortDescr = subject.shortDescr
def end = Math.min(shortDescr.size(), maxChars)
pushMobile.scriptParams['shortDescr'] = shortDescr.substring(0, end)

def comment = sourceObject
pushMobile.link << api.web.openCommentInList(subject.UUID, comment.UUID)

def author = comment.author
if(author) {
  pushMobile.toRemoveEmployee << author
}

pushMobile.scriptParams['root'] = utils.get('root', [:]).title