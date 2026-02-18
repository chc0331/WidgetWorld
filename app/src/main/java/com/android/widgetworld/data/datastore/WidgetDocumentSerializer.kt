package com.android.widgetworld.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.android.widgetworld.domain.model.emptyWidgetDocument
import com.android.widgetworld.proto.WidgetDocument
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * WidgetDocument의 Proto DataStore Serializer
 * 
 * Proto 메시지를 바이트 스트림으로 직렬화/역직렬화합니다.
 * DataStore는 이 Serializer를 사용하여 파일에 데이터를 저장하고 읽습니다.
 * 
 * 에러 처리:
 * - 파일이 손상된 경우 CorruptionException 발생
 * - 파일이 없거나 비어있는 경우 defaultValue 반환
 */
object WidgetDocumentSerializer : Serializer<WidgetDocument> {
    
    /**
     * 기본값: 빈 WidgetDocument
     * 
     * 다음 상황에서 사용됩니다:
     * - 앱 최초 실행 시 (파일이 없을 때)
     * - 파일이 손상되어 복구 불가능할 때
     */
    override val defaultValue: WidgetDocument = emptyWidgetDocument()
    
    /**
     * InputStream에서 WidgetDocument를 읽어옵니다.
     * 
     * @param input Proto 바이트 스트림
     * @return 역직렬화된 WidgetDocument
     * @throws CorruptionException Proto 파싱 실패 시
     */
    override suspend fun readFrom(input: InputStream): WidgetDocument {
        try {
            // Proto의 parseFrom()을 사용하여 바이트 스트림을 WidgetDocument로 변환
            return WidgetDocument.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            // Proto 형식이 잘못되었을 때 CorruptionException으로 래핑
            // DataStore는 이 예외를 받으면 파일을 삭제하고 defaultValue를 사용합니다
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
    
    /**
     * WidgetDocument를 OutputStream에 씁니다.
     * 
     * @param t 직렬화할 WidgetDocument
     * @param output Proto 바이트를 쓸 스트림
     */
    override suspend fun writeTo(t: WidgetDocument, output: OutputStream) {
        // Proto의 writeTo()를 사용하여 WidgetDocument를 바이트 스트림으로 변환
        t.writeTo(output)
    }
}

