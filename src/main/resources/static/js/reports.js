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

document.addEventListener('DOMContentLoaded', function () {
  const calendarEl = document.getElementById('calendar');
  const currentUserId = document.body.dataset.userId;
  const currentUserRole = document.body.dataset.userRole;

  const calendar = new FullCalendar.Calendar(calendarEl, {
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
      document.getElementById('eventId').value = event.id;
      document.getElementById('title').value = event.title;
      document.getElementById('start').value = toDatetimeLocalStringJST(event.start);
      document.getElementById('end').value = event.end ? toDatetimeLocalStringJST(event.end) : '';
      document.getElementById('description').value = event.extendedProps.description || '';

      const targetUserId = event.extendedProps.targetUserId;
      const userSelectEl = document.getElementById('userSelect');
      if (userSelectEl) {
        userSelectEl.value = targetUserId != null ? String(targetUserId) : '';
      }

	  // ...イベントデータのモーダルへのセットが終わった直後
      document.getElementById('eventModalLabel').textContent = '仕事内容の編集';
      document.getElementById('deleteEventBtn').style.display = 'inline-block';
	  
	  // コメント読み込みをここで実行
	  loadComments(event.id);
	  
	  // モーダルを表示
      new bootstrap.Modal(document.getElementById('eventModal')).show();
	  
	  function loadComments(eventId) {
	    fetch(`/api/events/${eventId}/comments`)
	      .then(response => response.json())
	      .then(comments => {
	        const commentList = document.getElementById('commentList');
	        commentList.innerHTML = ''; // 一旦リセット

	        comments.forEach(comment => {
	          const commentEl = document.createElement('div');
	          commentEl.classList.add('border-bottom', 'pb-1', 'mb-1');

	          // 自分のコメントかどうかで操作ボタンを切り替え
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

	        // 削除ボタンにイベントリスナーを追加
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
	                loadComments(document.getElementById('eventId').value); // 再読み込み
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

    }
  });

  calendar.render();

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
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
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
});
