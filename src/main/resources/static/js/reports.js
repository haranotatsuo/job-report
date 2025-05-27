// JavaScript でログイン中のユーザー情報を取得
const currentUserId = document.body.dataset.userId;
const currentUserRole = document.body.dataset.userRole;

console.log("現在のユーザーID:", currentUserId);
console.log("現在のロール:", currentUserRole);


function toDatetimeLocalStringJST(dateStrOrObj) {
  const date = typeof dateStrOrObj === 'string' ? new Date(dateStrOrObj) : dateStrOrObj;
  const offsetMin = date.getTimezoneOffset(); // JST補正
  const localDate = new Date(date.getTime() - offsetMin * 60 * 1000);
  return localDate.toISOString().slice(0, 16);
}

let calendar;

document.addEventListener('DOMContentLoaded', function () {
  const calendarEl = document.getElementById('calendar');
  const currentUserId = document.body.dataset.userId;
  const currentUserRole = document.body.dataset.userRole;

  calendar = new FullCalendar.Calendar(calendarEl, {
     initialView: window.innerWidth < 768 ? 'listWeek' : 'dayGridMonth',
    locale: 'ja',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    events: `/api/events?userId=${currentUserId}`,

	eventClick: function (info) {
	  const event = info.event;
	  const isStaff = currentUserRole === 'ROLE_STAFF';

	  // 詳細モーダルに共通情報をセット
	  document.getElementById('viewEventTitle').textContent = event.title;
	  document.getElementById('viewEventStart').textContent = new Date(event.start).toLocaleString();
	  document.getElementById('viewEventEnd').textContent = event.end ? new Date(event.end).toLocaleString() : '';
	  document.getElementById('viewEventDescription').textContent = event.extendedProps.description || '';
	  document.getElementById('viewEventUser').textContent = event.extendedProps.username || '不明';
	  
	  // 編集ボタンの表示・非表示
	  const editBtn = document.getElementById('editEventBtn');
	  if (isStaff) {
	    editBtn.style.display = 'inline-block';

	    // 編集モーダルを開く処理を準備（詳細モーダルではまだ開かない）
	    editBtn.onclick = function () {
	      document.getElementById('eventId').value = event.id;
	      document.getElementById('title').value = event.title;
	      document.getElementById('start').value = toDatetimeLocalStringJST(event.start);
	      document.getElementById('end').value = event.end ? toDatetimeLocalStringJST(event.end) : '';
	      document.getElementById('description').value = event.extendedProps.description || '';
	      document.getElementById('eventModalLabel').textContent = '仕事内容の編集';
	      document.getElementById('deleteEventBtn').style.display = 'inline-block';

	      const userSelectEl = document.getElementById('userSelect');
	      if (userSelectEl) {
	        const targetUserId = event.extendedProps.targetUserId;
	        userSelectEl.value = targetUserId != null ? String(targetUserId) : '';
	      }

	      loadComments(event.id, currentUserId);
	      bootstrap.Modal.getInstance(document.getElementById('viewEventModal')).hide(); // 詳細モーダルを閉じる
	      new bootstrap.Modal(document.getElementById('eventModal')).show(); // 編集モーダルを開く
	    };
	  } else {
	    editBtn.style.display = 'none';
	  }
	  document.getElementById('addCommentBtn').onclick = function () {
	  	const content = document.getElementById('newComment').value.trim();
		const eventId = document.getElementById('eventId').value || event.id;


	  	if (!content) {
	  	  alert("コメントを入力してください。");
	  	  return;
	  	}

	  	fetch(`/api/comments/event/${eventId}`, {
	  	  method: 'POST',
	  	  headers: {
	  	    'Content-Type': 'application/json',
	  	    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
	  	  },
	  	  body: JSON.stringify({ content })
	  	})
	  	.then(response => {
	  	  if (!response.ok) throw new Error('投稿に失敗しました');
	  	  return response.json();
	  	})
	  	.then(() => {
	  	  document.getElementById('newComment').value = '';
	  	  loadComments(eventId, currentUserId); // 再読み込み
	  	})
	  	.catch(error => {
	  	  alert('コメントの投稿に失敗しました: ' + error.message);
	  	});
	  };
	  // コメント読み込み
	  document.getElementById('eventId').value = event.id; // コメント用にセット
	  loadComments(event.id, currentUserId);

	  // 詳細モーダルを表示
	  const modalEl = document.getElementById('viewEventModal');
	  const modalInstance = new bootstrap.Modal(modalEl, {
	    backdrop: 'static',
	    keyboard: true
	  });
	  modalInstance.show();
	}


  });

  calendar.render();
});

// 🔽 外に出した loadComments 関数（currentUserIdを引数に）
function loadComments(eventId, currentUserId) {
  fetch(`/api/events/${eventId}/comments`)
    .then(response => response.json())
    .then(comments => {
      const commentList = document.getElementById('commentList');
      commentList.innerHTML = '';

      comments.forEach(comment => {
        const commentEl = document.createElement('div');
        commentEl.classList.add('border-bottom', 'pb-1', 'mb-1');

        const isMyComment = String(comment.userId) === currentUserId;
        commentEl.innerHTML = `
          <strong>${comment.username}</strong>（${new Date(comment.createdAt).toLocaleString()}）<br>
          <span class="comment-text">${comment.content}</span>
          ${isMyComment ? `
            <button class="btn btn-sm btn-link text-danger p-0 ms-2 delete-comment-btn" data-id="${comment.id}">削除</button>
          ` : ''}
        `;
        commentList.appendChild(commentEl);
      });

      // 削除ボタンイベント追加
      document.querySelectorAll('.delete-comment-btn').forEach(btn => {
        btn.addEventListener('click', function () {
          const commentId = this.dataset.id;
          if (confirm('このコメントを削除しますか？')) {
            fetch(`/api/comments/${commentId}`, {
              method: 'DELETE',
              headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
              }
            })
            .then(response => {
              if (!response.ok) throw new Error('削除に失敗しました');
              return response.text();
            })
            .then(() => {
              alert("コメントを削除しました");
              const currentEventId = document.getElementById('eventId').value;
              loadComments(currentEventId, currentUserId); // 再読み込み
            })
            .catch(error => {
              alert("エラー: " + error.message);
            });
          }
        });
      });
    })
    .catch(error => {
      console.error('コメントの取得に失敗しました:', error);
    });
}

  document.getElementById('addEventBtn').addEventListener('click', function () {
    document.getElementById('eventId').value = '';
    document.getElementById('title').value = '';
    document.getElementById('start').value = '';
    document.getElementById('end').value = '';
    document.getElementById('description').value = '';
    document.getElementById('eventModalLabel').textContent = '仕事内容の登録';
    document.getElementById('deleteEventBtn').style.display = 'none';
    document.getElementById('userSelect').selectedIndex = 0;
	document.getElementById('commentList').innerHTML = '';
  });

  document.getElementById('saveEventBtn').addEventListener('click', function () {
    const eventIdRaw = document.getElementById('eventId').value;
    const eventId = eventIdRaw !== '' ? Number(eventIdRaw) : null;
    const title = document.getElementById('title').value;
    const start = document.getElementById('start').value;
    const end = document.getElementById('end').value;
    const description = document.getElementById('description').value;
    const selectedUserId = document.getElementById('userSelect')?.value || currentUserId;

    if (currentUserRole === 'ROLE_STAFF' && (!selectedUserId || selectedUserId === '')) {
      alert("ユーザーを選択してください。");
      return;
    }

    if (end && start > end) {
      alert("終了日時は開始日時より後に設定してください。");
      return;
    }

    const eventData = {
      id: eventId,
      title,
      start,
      end,
      description,
      targetUserId: selectedUserId
    };

    const method = eventId ? 'PUT' : 'POST';
    const url = '/api/events' + (eventId ? `/${eventId}` : '');

    fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
      },
      body: JSON.stringify(eventData)
    })
    .then(response => {
      if (!response.ok) throw new Error('保存に失敗しました');
      return response.json();
    })
    .then(() => {
      bootstrap.Modal.getInstance(document.getElementById('eventModal')).hide();
      calendar.refetchEvents();
    })
    .catch(error => {
      alert("エラーが発生しました: " + error.message);
    });
  });

  document.getElementById('deleteEventBtn').addEventListener('click', function () {
    const eventId = document.getElementById('eventId').value;
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    if (!eventId) {
      alert("削除するイベントが選択されていません。");
      return;
    }

    if (!confirm("この予定を削除しますか？")) {
      return;
    }

    fetch(`/api/events/${eventId}`, {
      method: 'DELETE',
      headers: {
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
      }
    })
    .then(response => {
      if (!response.ok) throw new Error('サーバーエラー');
      return response.text();
    })
    .then(() => {
      const modal = bootstrap.Modal.getInstance(document.getElementById('eventModal'));
      modal.hide();
      alert("イベントを削除しました");
      calendar.refetchEvents();
    })
    .catch(error => {
      alert('削除に失敗しました: ' + error.message);
    });
  });
