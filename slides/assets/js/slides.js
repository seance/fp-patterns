$(function() {

  function prepareSnippets() {
    $('.code').each(function(i, e) {
      var elem = $(e),
          lines = (elem.text().match(/\n/g) || []).length
      
      elem.css({height: lines * 16})
      elem.text(elem.text().trim())
      
      var editor = ace.edit(e)
      editor.getSession().setMode("ace/mode/scala")
      editor.setTheme("ace/theme/slides")
      editor.setReadOnly(true)
      editor.setDisplayIndentGuides(false)
      editor.setHighlightActiveLine(false)
      editor.setHighlightGutterLine(false)

      window.editor = editor
    })
  }
  
  function prepareExamples() {
    $('.example').each(function(i, e) {
      var name = $(e).data('name'),
          html = '<span class="bigquote">“</span>' + name +
                 '<span class="bigquote">”</span>',
          prompt = 'Meditate on the following to proceed:'
      
      $('<p>', { class: 'prompt', text: prompt }).appendTo(e)
      $('<p>', { class: 'name', html: html }).appendTo(e)
      $('<p>', { class: 'message' }).appendTo(e)
      $(e).addClass('pristine')
    })
    
    $('.prompt').first().show()
  }
  
  function processMessage(m) {
    console.log(m)
    
    var example = $('.example[data-name="' + m.name + '"]')
      .removeClass().addClass('example')
    
    if (m.success) {
      example.addClass('success')
      $('.prompt').hide()
      
    } else {
      if (m.message === 'an implementation is missing') {
        $('.prompt').hide()
        example.addClass('pristine')
        example.find('.prompt').show()
      } else {
        example.addClass('failure')
        example.find('.message').text(m.message)
      }
    }
    
    var story = example.parent('.story')
    
    if (!story.find('.example.pristine, .example.failure').length) {
      story.addClass('complete').next().fadeIn('slow')
    }
  }
  
  function receiveMessages() {
    var ws = new WebSocket("ws://localhost:8001")
    ws.onmessage = function(e) {
      processMessage(JSON.parse(e.data))
    }
  }
  
  prepareSnippets()
  prepareExamples()
  receiveMessages()
})