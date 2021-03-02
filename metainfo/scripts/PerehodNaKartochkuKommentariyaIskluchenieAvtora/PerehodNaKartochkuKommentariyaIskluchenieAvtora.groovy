def comment = sourceObject
pushMobile.link << api.web.openCommentInList(subject.UUID, comment.UUID)
def author = comment.author
if(author) {
  pushMobile.toRemoveEmployee << author
}
pushMobile.scriptParams['root'] = utils.get('root', [:]).title